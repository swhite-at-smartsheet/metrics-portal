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
            final PagerDutyEndpointRepository pagerDutyEndpointRepository,
            final OrganizationProvider organizationProvider) {
        this(configuration.getInt("pagerDutyEndpoints.limit"), pagerDutyEndpointRepository, organizationProvider);
    }

    /**
     * Adds a pagerduty endpoint in the repository.
     *
     * @return Ok if the endpoint was created or updated successfully, a failure HTTP status code otherwise.
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
     * Get specific pagerduty endpoint.
     *
     * @param name The name of the endpoint.
     * @return Matching alert.
     */
    public Result get(final String name) {
        final Organization organization = _organizationProvider.getOrganization(request());
        final Optional<PagerDutyEndpoint> result = _pagerDutyEndpointRepository.get(name, organization);
        if (!result.isPresent()) {
            return notFound();
        }
        // Return as JSON
        return ok(Json.toJson(result.get().toView()));
    }

    /**
     * Query for pagerduty endpoints.
     *
     * @param contains The text to search for. Optional.
     * @param limit The maximum number of results to return. Optional.
     * @param offset The number of results to skip. Optional.
     * @return <code>Result</code> paginated matching notification groups.
     */
    // CHECKSTYLE.OFF: ParameterNameCheck - Names must match query parameters.
    public Result query(
            final String contains,
            final Integer limit,
            final Integer offset) {
        // CHECKSTYLE.ON: ParameterNameCheck

        // Convert and validate parameters
        final Optional<String> argContains = Optional.ofNullable(contains);
        final Optional<Integer> argOffset = Optional.ofNullable(offset);
        final int argLimit = Math.min(_maxLimit, Optional.of(MoreObjects.firstNonNull(limit, _maxLimit)).get());
        if (argLimit < 0) {
            return badRequest("Invalid limit; must be greater than or equal to 0");
        }
        if (argOffset.isPresent() && argOffset.get() < 0) {
            return badRequest("Invalid offset; must be greater than or equal to 0");
        }

        // Build conditions map
        final Map<String, String> conditions = Maps.newHashMap();
        if (argContains.isPresent()) {
            conditions.put("contains", argContains.get());
        }

        // Build a endpoint repository query
        final PagerDutyEndpointQuery query = _pagerDutyEndpointRepository.createQuery(_organizationProvider.getOrganization(request()))
                .contains(argContains)
                .limit(argLimit)
                .offset(argOffset);

        // Execute the query
        final QueryResult<PagerDutyEndpoint> result;
        try {
            result = query.execute();
            // CHECKSTYLE.OFF: IllegalCatch - Convert any exception to 500
        } catch (final Exception e) {
            // CHECKSTYLE.ON: IllegalCatch
            LOGGER.error()
                    .setMessage("Notification group query failed")
                    .setThrowable(e)
                    .log();
            return internalServerError();
        }

        // Wrap the query results and return as JSON
        if (result.etag().isPresent()) {
            response().setHeader(HttpHeaders.ETAG, result.etag().get());
        }
        return ok(Json.toJson(new PagedContainer<>(
                result.values()
                        .stream()
                        .map(PagerDutyEndpoint::toView)
                        .collect(Collectors.toList()),
                new Pagination(
                        request().path(),
                        result.total(),
                        result.values().size(),
                        argLimit,
                        argOffset,
                        conditions))));
    }

    /**
     * Delete a specific pagerduty endpoint.
     *
     * @param name The name of the endpoint.
     * @return No content
     */
    public Result delete(final String name) {
        final Organization organization = _organizationProvider.getOrganization(request());
        final int deleted = _pagerDutyEndpointRepository.delete(name, organization);
        if (deleted > 0) {
            return noContent();
        } else {
            return notFound();
        }
    }

    private PagerDutyEndpointController(
            final int maxLimit,
            final PagerDutyEndpointRepository pagerDutyEndpointRepository,
            final OrganizationProvider organizationProvider) {
        _maxLimit = maxLimit;
        _pagerDutyEndpointRepository = pagerDutyEndpointRepository;
        _organizationProvider = organizationProvider;
    }

    private final int _maxLimit;
    private final PagerDutyEndpointRepository _pagerDutyEndpointRepository;
    private final OrganizationProvider _organizationProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(PagerDutyEndpointController.class);
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.getInstance();
}
