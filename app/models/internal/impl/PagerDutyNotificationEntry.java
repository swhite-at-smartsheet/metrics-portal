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
import com.arpnetworking.mql.grammar.AlertTrigger;
import com.arpnetworking.steno.Logger;
import com.arpnetworking.steno.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import models.internal.Alert;
import models.internal.NotificationEntry;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Internal model representing a pagerduty notification entry.
 * These do not have any internal state and act as singletons when creating a NotificationGroup.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public final class PagerDutyNotificationEntry implements NotificationEntry {
    @Override
    public CompletionStage<Void> notifyRecipient(final Alert alert, final AlertTrigger trigger, final Injector injector) {
        LOGGER.debug().setMessage("executing pagerduty call").log();
        final Config typesafeConfig = injector.getInstance(Config.class);
        String pagerDutyEndpoint = typesafeConfig.getString("pagerDuty.uri");
        final String pagerDutyServiceKey = typesafeConfig.getString("pagerDuty.serviceKey");
        final String subject = String.format("Alert '%s' on %s in alarm ", alert.getName(), getGroupByString(trigger));
        final String baseUrl = typesafeConfig.getString("alerts.baseUrl");
        final String alertUrl = URI.create(baseUrl).resolve("/#alert/edit/" + alert.getId()).toString();

        try {
            final URI pagerDutyURI = new URI(pagerDutyEndpoint);
            final ActorSystem actorSystem = injector.getInstance(ActorSystem.class);
            final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);
            final Http http = Http.get(actorSystem);
            final PagerDutyContext context = new PagerDutyContext.Builder()
                    .setType("link")
                    .setHref(alertUrl)
                    .setText("View the alert in M-Portal")
                    .build();
            final PagerDutyContext[] contexts = { context };
            final PagerDutyAlert pagerDutyAlert = new PagerDutyAlert.Builder()
                    .setServiceKey(pagerDutyServiceKey)
                    .setEventType("trigger")
                    .setDescription(subject)
                    .setAlert(alert)
                    .setTrigger(trigger)
                    .setAlertUrl(alertUrl)
                    .setContexts(contexts)
                    .build();
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
            final CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public models.view.NotificationEntry toView() {
        return new models.view.PagerDutyNotificationEntry();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass()); // act like a singleton
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        // only allow a single instance
        return o != null && getClass() == o.getClass();
    }

    private PagerDutyNotificationEntry(final Builder builder) {
    }

    private String getGroupByString(final AlertTrigger trigger) {
        return trigger.getGroupBy()
                .entrySet()
                .stream()
                .map(entry -> String.format("%s %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));
    }

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
    }
}
