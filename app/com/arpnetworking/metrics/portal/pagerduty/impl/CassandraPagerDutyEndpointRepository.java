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
import com.datastax.driver.mapping.Result;
import com.google.inject.Inject;
import models.internal.*;
import models.internal.impl.DefaultPagerDutyEndpointQuery;
import models.internal.impl.DefaultQueryResult;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A pagerduty endpoint repository backed by cassandra.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public class CassandraPagerDutyEndpointRepository implements PagerDutyEndpointRepository {
    /**
     * Public constructor.
     *
     * @param cassandraSession a Session to use to query data
     * @param mappingManager a MappingManager providing ORM for the Cassandra objects
     */
    @Inject
    public CassandraPagerDutyEndpointRepository(final Session cassandraSession, final MappingManager mappingManager) {
        _cassandraSession = cassandraSession;
        _mappingManager = mappingManager;
    }

    @Override
    public void open() {
        assertIsOpen(false);
        LOGGER.debug().setMessage("Opening notification repository").log();
        _isOpen.set(true);
    }

    @Override
    public void close() {
        assertIsOpen();
        LOGGER.debug().setMessage("Closing notification repository").log();
        _isOpen.set(false);
        _cassandraSession.close();
    }

    @Override
    public Optional<PagerDutyEndpoint> get(UUID identifier, Organization organization) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Getting pagerduty endpoint")
                .addData("identifier", identifier)
                .addData("organization", organization)
                .log();
        final Mapper<models.cassandra.PagerDutyEndpoint> mapper = _mappingManager.mapper(models.cassandra.PagerDutyEndpoint.class);
        final models.cassandra.PagerDutyEndpoint cassandraPagerDutyEndpoint = mapper.get(identifier);

        if (cassandraPagerDutyEndpoint == null) {
            return Optional.empty();
        }

        return Optional.of(cassandraPagerDutyEndpoint.toInternal());
    }

    @Override
    public void upsert(final PagerDutyEndpoint pagerDutyEndpoint, final Organization organization) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Upserting pagerduty endpoint")
                .addData("endpoint", pagerDutyEndpoint)
                .log();

        final Mapper<models.cassandra.PagerDutyEndpoint> mapper = _mappingManager.mapper(models.cassandra.PagerDutyEndpoint.class);
        models.cassandra.PagerDutyEndpoint cassandraPagerDutyEndpoint = mapper.get(pagerDutyEndpoint.getName());

        if (cassandraPagerDutyEndpoint == null) {
            cassandraPagerDutyEndpoint = new models.cassandra.PagerDutyEndpoint();
        }

        cassandraPagerDutyEndpoint.setName(pagerDutyEndpoint.getName());
        cassandraPagerDutyEndpoint.setPagerDutyUrl(pagerDutyEndpoint.getPagerDutyUrl());
        cassandraPagerDutyEndpoint.setServiceKey(pagerDutyEndpoint.getServiceKey());
        cassandraPagerDutyEndpoint.setComment(pagerDutyEndpoint.getComment());
        cassandraPagerDutyEndpoint.setOrganization(organization.getId());

        mapper.save(cassandraPagerDutyEndpoint);
    }

    @Override
    public PagerDutyEndpointQuery createQuery(final Organization organization) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Preparing query")
                .addData("organization", organization)
                .log();
        return new DefaultPagerDutyEndpointQuery(this, organization);
    }

    @Override
    public QueryResult<PagerDutyEndpoint> query(final PagerDutyEndpointQuery query) {
        final Mapper<models.cassandra.PagerDutyEndpoint> mapper = _mappingManager.mapper(models.cassandra.PagerDutyEndpoint.class);
        final models.cassandra.PagerDutyEndpoint.PagerDutyEndpointQueries accessor = mapper.getManager().createAccessor(models.cassandra.PagerDutyEndpoint.PagerDutyEndpointQueries.class);
        final Result<models.cassandra.PagerDutyEndpoint> result = accessor.getEndpointsForOrganization(query.getOrganization().getId());
        final Spliterator<models.cassandra.PagerDutyEndpoint> allEndpoints = result.spliterator();
        final int start = query.getOffset().orElse(0);

        Stream<models.cassandra.PagerDutyEndpoint> endpointStream = StreamSupport.stream(allEndpoints, false);

        if (query.getContains().isPresent()) {
            endpointStream = endpointStream.filter(endpoint -> {
                final String contains = query.getContains().get().toLowerCase(Locale.ENGLISH);
                return endpoint.getName().toLowerCase(Locale.ENGLISH).contains(contains);
            });
        }

        final List<PagerDutyEndpoint> endpoints = endpointStream
                .map(endpoint -> endpoint.toInternal())
                .collect(Collectors.toList());
        final List<PagerDutyEndpoint> paginated = endpoints.stream().skip(start).limit(query.getLimit()).collect(Collectors.toList());
        return new DefaultQueryResult<>(paginated, endpoints.size());
    }

    @Override
    public int delete(UUID identifier, final Organization organization) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Deleting pagerduty endpoint")
                .addData("identifier", identifier)
                .addData("organization", organization)
                .log();
        final Optional<PagerDutyEndpoint> pagerDutyEndpoint = get(identifier, organization);
        if (pagerDutyEndpoint.isPresent()) {
            final Mapper<models.cassandra.PagerDutyEndpoint> mapper = _mappingManager.mapper(models.cassandra.PagerDutyEndpoint.class);
            mapper.delete(identifier);
            return 1;
        }
        return 0;
    }

    private void assertIsOpen() {
        assertIsOpen(true);
    }

    private void assertIsOpen(final boolean expectedState) {
        if (_isOpen.get() != expectedState) {
            throw new IllegalStateException(String.format("PagerDutyEndpoint repository is not %s", expectedState ? "open" : "closed"));
        }
    }

    private final Session _cassandraSession;
    private final MappingManager _mappingManager;
    private final AtomicBoolean _isOpen = new AtomicBoolean(false);

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraPagerDutyEndpointRepository.class);
}
