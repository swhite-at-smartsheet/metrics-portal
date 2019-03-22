/*
 * Copyright 2017 Smartsheet.com
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
package com.arpnetworking.metrics.portal.pagerduty.impl;

import com.arpnetworking.metrics.portal.pagerduty.PagerDutyEndpointRepository;
import com.arpnetworking.steno.Logger;
import com.arpnetworking.steno.LoggerFactory;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import models.internal.PagerDutyEndpoint;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of an {@link PagerDutyEndpointRepository} that stores the data in Cassandra.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public final class CassandraPagerDutyEndpointRepository implements PagerDutyEndpointRepository {
    /**
     * Public constructor.
     *
     * @param cassandraSession a Session to use to query data
     * @param mappingManager a MappingManager providing ORM for the Cassandra objects
     * @param pagerDutyEndpointRepository PagerDutyEndpointRepository repository used to create and lookup pagerduty endpoints
     */
    @Inject
    public CassandraPagerDutyEndpointRepository(
            final Session cassandraSession,
            final MappingManager mappingManager,
            final PagerDutyEndpointRepository pagerDutyEndpointRepository) {
        _cassandraSession = cassandraSession;
        _mappingManager = mappingManager;
        _pagerDutyEndpointRepository = pagerDutyEndpointRepository;
    }

    @Override
    public void open() {
        assertIsOpen(false);
        LOGGER.debug().setMessage("Opening pagerduty endpoint repository").log();
        _isOpen.set(true);
    }

    @Override
    public void close() {
        assertIsOpen();
        LOGGER.debug().setMessage("Closing pagerduty endpoint repository").log();
        _isOpen.set(false);
        _cassandraSession.close();
    }

    @Override
    public Optional<PagerDutyEndpoint> get(final UUID identifier) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Getting alert")
                .addData("pagerDutyEndpointId", identifier)
                .log();
        final Mapper<models.cassandra.PagerDutyEndpoint> mapper = _mappingManager.mapper(models.cassandra.PagerDutyEndpoint.class);
        final models.cassandra.PagerDutyEndpoint cassandraPagerDutyEndpoint = mapper.get(identifier);

        if (cassandraPagerDutyEndpoint == null) {
            return Optional.empty();
        }

        return Optional.of(cassandraPagerDutyEndpoint.toInternal());
    }

    @Override
    public int delete(final UUID identifier) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Deleting alert")
                .addData("alertId", identifier)
                .log();
        final Optional<PagerDutyEndpoint> pagerDutyEndpoint = get(identifier);
        if (pagerDutyEndpoint.isPresent()) {
            final Mapper<models.cassandra.PagerDutyEndpoint> mapper = _mappingManager.mapper(models.cassandra.PagerDutyEndpoint.class);
            mapper.delete(identifier);
            return 1;
        } else {
            return 0;
        }
    }

//    @Override
//    public QueryResult<Alert> query(final AlertQuery query) {
//        final Mapper<models.cassandra.Alert> mapper = _mappingManager.mapper(models.cassandra.Alert.class);
//        final models.cassandra.Alert.AlertQueries accessor = mapper.getManager().createAccessor(models.cassandra.Alert.AlertQueries.class);
//        final Result<models.cassandra.Alert> result = accessor.getAlertsForOrganization(query.getOrganization().getId());
//        final Spliterator<models.cassandra.Alert> allAlerts = result.spliterator();
//        final int start = query.getOffset().orElse(0);
//
//        Stream<models.cassandra.Alert> alertStream = StreamSupport.stream(allAlerts, false);
//
//        if (query.getContains().isPresent()) {
//            alertStream = alertStream.filter(alert -> {
//                final String contains = query.getContains().get().toLowerCase(Locale.ENGLISH);
//                return alert.getQuery().toLowerCase(Locale.ENGLISH).contains(contains)
//                        || alert.getName().toLowerCase(Locale.ENGLISH).contains(contains);
//            });
//        }
//
//        final List<Alert> alerts = alertStream
//                .map(alert -> alert.toInternal(_notificationRepository))
//                .collect(Collectors.toList());
//        final List<Alert> paginated = alerts.stream().skip(start).limit(query.getLimit()).collect(Collectors.toList());
//        return new DefaultQueryResult<>(paginated, alerts.size());
//    }
//
//    @Override
//    public long getAlertCount(final Organization organization) {
//        final Mapper<models.cassandra.Alert> mapper = _mappingManager.mapper(models.cassandra.Alert.class);
//        final models.cassandra.Alert.AlertQueries accessor = mapper.getManager().createAccessor(models.cassandra.Alert.AlertQueries.class);
//        final Result<models.cassandra.Alert> result = accessor.getAlertsForOrganization(organization.getId());
//        return StreamSupport.stream(result.spliterator(), false).count();
//
//    }

    @Override
    public void addOrUpdatePagerDutyEndpoint(final PagerDutyEndpoint pagerDutyEndpoint) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Upserting pagerduty endpoint")
                .addData("endpoint", pagerDutyEndpoint)
                .log();

        final models.cassandra.PagerDutyEndpoint cassPagerDutyEndpoint = new models.cassandra.PagerDutyEndpoint();
        cassPagerDutyEndpoint.setName(pagerDutyEndpoint.getName());
        cassPagerDutyEndpoint.setAddress(pagerDutyEndpoint.getAddress());
        cassPagerDutyEndpoint.setServiceKey(pagerDutyEndpoint.getServiceKey());

        final Mapper<models.cassandra.PagerDutyEndpoint> mapper = _mappingManager.mapper(models.cassandra.PagerDutyEndpoint.class);
        mapper.save(cassPagerDutyEndpoint);
    }

    private void assertIsOpen() {
        assertIsOpen(true);
    }

    private void assertIsOpen(final boolean expectedState) {
        if (_isOpen.get() != expectedState) {
            throw new IllegalStateException(String.format("Alert repository is not %s", expectedState ? "open" : "closed"));
        }
    }

    private final Session _cassandraSession;
    private final MappingManager _mappingManager;
    private final AtomicBoolean _isOpen = new AtomicBoolean(false);
    private final PagerDutyEndpointRepository _pagerDutyEndpointRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraPagerDutyEndpointRepository.class);
}
