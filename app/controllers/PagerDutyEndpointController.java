/*
 * Copyright 2015 Groupon.com
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
package controllers;

import akka.actor.ActorRef;
import com.arpnetworking.commons.jackson.databind.ObjectMapperFactory;
import com.arpnetworking.metrics.portal.alerts.AlertRepository;
import com.arpnetworking.metrics.portal.alerts.impl.AlertExecutor;
import com.arpnetworking.metrics.portal.notifications.NotificationRepository;
import com.arpnetworking.metrics.portal.organizations.OrganizationProvider;
import com.arpnetworking.metrics.portal.pagerduty.PagerDutyEndpointRepository;
import com.arpnetworking.steno.Logger;
import com.arpnetworking.steno.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import models.internal.*;
import models.view.PagedContainer;
import models.view.Pagination;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Metrics portal pagerduty endpoint controller. Exposes APIs to query and manipulate alerts.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
@Singleton
public class PagerDutyEndpointController extends Controller {

    /**
     * Public constructor.
     *
     * @param configuration Instance of Play's {@link Config}.
     * @param pagerDutyEndpointRepository Instance of {@link PagerDutyEndpointRepository}.
     */
    @Inject
    public PagerDutyEndpointController(
            final Config configuration,
            final PagerDutyEndpointRepository pagerDutyEndpointRepository) {
        _pagerDutyEndpointRepository = pagerDutyEndpointRepository;

    }

    /**
     * Adds an alert in the alert repository.
     *
     * @return Ok if the alert was created or updated successfully, a failure HTTP status code otherwise.
     */
    public Result addOrUpdate() {
        final PagerDutyEndpoint pagerDutyEndpoint;
        try {
            final JsonNode jsonBody = request().body().asJson();
            if (jsonBody == null) {
                return badRequest("Missing request body.");
            }
            final models.view.PagerDutyEndpoint viewPagerDutyEndpoint = OBJECT_MAPPER.treeToValue(jsonBody, models.view.PagerDutyEndpoint.class);
            pagerDutyEndpoint = viewPagerDutyEndpoint.toInternal(OBJECT_MAPPER);
            // CHECKSTYLE.OFF: IllegalCatch - Convert any exception to 400
        } catch (final RuntimeException | JsonProcessingException e) {
            // CHECKSTYLE.ON: IllegalCatch
            LOGGER.error()
                    .setMessage("Failed to build an pagerduty endpoint.")
                    .setThrowable(e)
                    .log();
            return badRequest("Invalid request body.");
        }

        return noContent();
    }

    /**
     * Get specific alert.
     *
     * @param id The identifier of the alert.
     * @return Matching alert.
     */
    public Result get(final String id) {
        final UUID identifier;
        try {
            identifier = UUID.fromString(id);
        } catch (final IllegalArgumentException e) {
            return badRequest();
        }
        final Optional<PagerDutyEndpoint> result = _pagerDutyEndpointRepository.get(identifier);
        if (!result.isPresent()) {
            return notFound();
        }
        // Return as JSON
        return ok(Json.toJson(result.get().toView()));
    }

    /**
     * Delete a specific alert.
     *
     * @param id The identifier of the alert.
     * @return No content
     */
    public Result delete(final String id) {
        final UUID identifier = UUID.fromString(id);
        final int deleted = _pagerDutyEndpointRepository.delete(identifier);
        if (deleted > 0) {
            return noContent();
        } else {
            return notFound();
        }
    }

    private final PagerDutyEndpointRepository _pagerDutyEndpointRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PagerDutyEndpointController.class);
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.getInstance();
}
