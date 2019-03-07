/**
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import models.internal.Alert;
import models.internal.NotificationEntry;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

/**
 * Internal model representing a pagerduty notification entry.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public final class PagerDutyNotificationEntry implements NotificationEntry {
    @Override
    public CompletionStage<Void> notifyRecipient(final Alert alert, final AlertTrigger trigger, final Injector injector) {
        LOGGER.debug().setMessage("executing pagerduty call").addData("address", _address).log();
        final Config typesafeConfig = injector.getInstance(Config.class);
        String pagerDutyEndpoint = typesafeConfig.getString("pagerDuty.uri");
        String pagerDutyServiceKey = typesafeConfig.getString("pagerDuty.serviceKey");

        try {
            URI pagerDutyURI = new URI(pagerDutyEndpoint);

            final ActorSystem actorSystem = injector.getInstance(ActorSystem.class);
            final ObjectMapper mapper = injector.getInstance(ObjectMapper.class);
            final Http http = Http.get(actorSystem);
            final ObjectNode body = mapper.createObjectNode();
            body.put("service_key", pagerDutyServiceKey);
            body.put("event_type", "trigger");
            body.put("client", "mportal");
            body.put("description", alert.getName());
            body.set("alert", mapper.valueToTree(alert));
            body.set("trigger", mapper.valueToTree(trigger));

            return http
                    .singleRequest(
                            HttpRequest.POST(pagerDutyURI.toASCIIString())
                                    .withEntity(HttpEntities.create(ContentTypes.APPLICATION_JSON, body.toString())))
                    .thenApply(response -> {
                        if (response.status().isFailure()) {
                            LOGGER.error("Error posting to pagerduty: " + response.toString());
                        }
                        return null;
                    });
        } catch (Exception e) {
            LOGGER.error("notifyException() exception: " + e);
        }
        return null;
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
        // only allow a single instance
        return o != null && getClass() == o.getClass();
    }

    public String getAddress() {
        return _address;
    }

    private PagerDutyNotificationEntry(final Builder builder) {
        _address = builder._address;
    }

    private final String _address;

    private static final Logger LOGGER = LoggerFactory.getLogger(PagerDutyNotificationEntry.class);

    /**
     * Implementation of the builder pattern for a {@link PagerDutyNotificationEntry}.
     *
     * @author Brandon Arp (brandon dot arp at smartsheet dot com)
     */
    public static final class Builder extends OvalBuilder<PagerDutyNotificationEntry> {
        /**
         * Public constructor.
         */
        public Builder() {
            super(PagerDutyNotificationEntry::new);
        }

        /**
         * The pagerduty address. Required. Cannot be null or empty.
         *
         * @param value The email address.
         * @return This instance of {@link Builder}.
         */
        public Builder setAddress(final String value) {
            _address = value;
            return this;
        }

        private String _address;
    }
}
