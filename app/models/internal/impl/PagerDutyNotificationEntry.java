/*
 * Copyright 2018 Smartsheet.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models.internal.impl;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpRequest;
import com.arpnetworking.commons.builder.OvalBuilder;
import com.arpnetworking.metrics.portal.pagerduty.PagerDutyEndpointRepository;
import com.arpnetworking.mql.grammar.AlertTrigger;
import com.arpnetworking.steno.Logger;
import com.arpnetworking.steno.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import models.internal.Alert;
import models.internal.NotificationEntry;
import models.internal.PagerDutyEndpoint;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Internal model representing a pagerduty notification entry.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public final class PagerDutyNotificationEntry implements NotificationEntry {
    @Override
    public CompletionStage<Void> notifyRecipient(final Alert alert, final AlertTrigger trigger, final Injector injector) {
        LOGGER.debug().setMessage("executing pagerduty call").log();
        final Config typesafeConfig = injector.getInstance(Config.class);
        final PagerDutyEndpointRepository pagerDutyEndpointRepository = injector.getInstance(PagerDutyEndpointRepository.class);
        String pagerDutyNotificationName = getAddress(); // FIXME code smell...
        final Optional<PagerDutyEndpoint> pde = pagerDutyEndpointRepository.getByName(getAddress(), alert.getOrganization());
        if (!pde.isPresent()) {
            String error = "notifyRecipient() error: pagerduty endpoint '" + pagerDutyNotificationName + "' not found in database";
            LOGGER.error(error);
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new Throwable(error));
            return future;
        }

        // TODO lookup endpoint record from DB, get url and service key...
        String pagerDutyUrl = pde.get().getPagerDutyUrl();
        String pagerDutyServiceKey = pde.get().getServiceKey();

        try {
            final String baseUrl = typesafeConfig.getString("alerts.baseUrl");
            final URI pagerDutyURI = new URI(pagerDutyUrl);
            final ActorSystem actorSystem = injector.getInstance(ActorSystem.class);
            final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);
            final Http http = Http.get(actorSystem);
            final PagerDutyAlert pagerDutyAlert = createPagerDutyAlert(baseUrl, pagerDutyUrl, pagerDutyServiceKey, alert, trigger);

            return http
                    .singleRequest(
                            HttpRequest.POST(pagerDutyURI.toASCIIString())
                                    .withEntity(HttpEntities.create(ContentTypes.APPLICATION_JSON, mapper.writeValueAsString(pagerDutyAlert))))
                    .thenApply(response -> {
                        if (response.status().isFailure()) {
                            LOGGER.error("Error posting to pagerduty: " + response.toString());
                        }
                        return null;
                    });
        } catch (final Exception e) {
            LOGGER.error("notifyRecipient() exception: " + e);
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private PagerDutyAlert createPagerDutyAlert(final String baseUrl, final String pagerDutyUrl, final String pagerDutyServiceKey, final Alert alert, final AlertTrigger alertTrigger) {
        final PagerDutyAlert pagerDutyAlert = new PagerDutyAlert();
        final String subject = String.format("Alert '%s' on %s in alarm ", alert.getName(), getGroupByString(alertTrigger));
        final String alertUrl = URI.create(baseUrl).resolve("/#alert/edit/" + alert.getId()).toString();

        PagerDutyContext[] contexts = { new PagerDutyContext("link", alertUrl, "View the alert in M-Portal") };

        pagerDutyAlert.setServiceKey(pagerDutyServiceKey);
        pagerDutyAlert.setEventType("trigger");
        pagerDutyAlert.setDescription(subject);
        pagerDutyAlert.setAlert(alert);
        pagerDutyAlert.setTrigger(alertTrigger);
        pagerDutyAlert.setAlertUrl(alertUrl);
        pagerDutyAlert.setContexts(contexts);

        return pagerDutyAlert;
    }

    @Override
    public models.view.NotificationEntry toView() {
        final models.view.PagerDutyNotificationEntry view = new models.view.PagerDutyNotificationEntry();
        view.setAddress(_address);
        return view;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_address);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PagerDutyNotificationEntry other = (PagerDutyNotificationEntry) o;
        return Objects.equals(_address, other._address);
    }

    public String getAddress() {
        return _address;
    }

    private PagerDutyNotificationEntry(final Builder builder) {
        _address = builder._address;
    }

    private String getGroupByString(final AlertTrigger trigger) {
        return trigger.getGroupBy()
                .entrySet()
                .stream()
                .map(entry -> String.format("%s %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    private final String _address;

    private static final Logger LOGGER = LoggerFactory.getLogger(PagerDutyNotificationEntry.class);

    /**
     * Implementation of the builder pattern for a {@link PagerDutyNotificationEntry}.
     *
     * @author Sheldon White (sheldon.white at smartsheet dot com)
     */
    public static final class Builder extends OvalBuilder<PagerDutyNotificationEntry> {
        /**
         * Public constructor.
         */
        public Builder() {
            super(PagerDutyNotificationEntry::new);
        }

        /**
         * The address. Required. Cannot be null or empty.
         *
         * @param value The email address.
         * @return This instance of {@link PagerDutyNotificationEntry.Builder}.
         */
        public PagerDutyNotificationEntry.Builder setAddress(final String value) {
            _address = value;
            return this;
        }
        @NotNull
        @NotEmpty
        private String _address;
    }

    /**
     * Model classes for a PagerDuty alert and the embedded context objects, as defined in their API document:
     * https://v2.developer.pagerduty.com/docs/events-api
     */
    private class PagerDutyAlert {
        @JsonProperty("service_key")
        private String serviceKey;

        @JsonProperty("event_type")
        private String eventType;

        @JsonProperty("description")
        private String description;

        @JsonProperty("alert")
        private Alert alert;

        @JsonProperty("trigger")
        private AlertTrigger trigger;

        @JsonProperty("alertUrl")
        private String alertUrl;

        @JsonProperty("contexts")
        private PagerDutyContext[] contexts;

        public String getServiceKey() {
            return serviceKey;
        }

        public void setServiceKey(String serviceKey) {
            this.serviceKey = serviceKey;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Alert getAlert() {
            return alert;
        }

        public void setAlert(Alert alert) {
            this.alert = alert;
        }

        public AlertTrigger getTrigger() {
            return trigger;
        }

        public void setTrigger(AlertTrigger trigger) {
            this.trigger = trigger;
        }

        public String getAlertUrl() {
            return alertUrl;
        }

        public void setAlertUrl(String alertUrl) {
            this.alertUrl = alertUrl;
        }

        public PagerDutyContext[] getContexts() {
            return contexts;
        }

        public void setContexts(PagerDutyContext[] contexts) {
            this.contexts = contexts;
        }
    }

    private class PagerDutyContext {
        private String type;
        private String href;
        private String text;

        public PagerDutyContext(final String type, final String href, final String text) {
            this.type = type;
            this.href = href;
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public String getHref() {
            return href;
        }

        public String getText() {
            return text;
        }
    }
}
