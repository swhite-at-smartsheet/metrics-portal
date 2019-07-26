/*
 * Copyright 2019 Smartsheet
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
import com.arpnetworking.play.configuration.ConfigurationHelper;
import com.arpnetworking.steno.Logger;
import com.arpnetworking.steno.LoggerFactory;
import com.typesafe.config.Config;
import io.ebean.*;
import models.internal.*;
import models.internal.impl.DefaultPagerDutyEndpointQuery;
import models.internal.impl.DefaultQueryResult;
import play.Environment;
import play.db.ebean.EbeanDynamicEvolutions;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A pagerduty endpoint repository backed by a database.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public class DatabasePagerDutyEndpointRepository implements PagerDutyEndpointRepository {
    /**
     * Public constructor.
     *
     * @param environment Play's <code>Environment</code> instance.
     * @param config Play's <code>Configuration</code> instance.
     * @param ignored ignored, used as dependency injection ordering
     * @throws Exception If the configuration is invalid.
     */
    @Inject
    public DatabasePagerDutyEndpointRepository(
            final Environment environment,
            final Config config,
            final EbeanDynamicEvolutions ignored) throws Exception {
        this(
                ConfigurationHelper.<DatabasePagerDutyEndpointRepository.PagerDutyEndpointQueryGenerator>getType(
                        environment,
                        config,
                        "pagerDutyEndpointRepository.pagerDutyEndpointQueryGenerator.type")
                        .getDeclaredConstructor()
                        .newInstance());
    }

    /**
     * Public constructor.
     *
     * @param queryGenerator Instance of <code>PagerDutyEndpointQueryGenerator</code>.
     */
    public DatabasePagerDutyEndpointRepository(final DatabasePagerDutyEndpointRepository.PagerDutyEndpointQueryGenerator queryGenerator) {
        _pagerDutyEndpointQueryGenerator = queryGenerator;
    }

    @Override
    public void open() {
        assertIsOpen(false);
        LOGGER.debug().setMessage("Opening pagerduty endpoints repository").log();
        _isOpen.set(true);
    }

    @Override
    public void close() {
        assertIsOpen();
        LOGGER.debug().setMessage("Closing pagerduty endpoints repository").log();
        _isOpen.set(false);
    }

    @Override
    public Optional<PagerDutyEndpoint> get(final UUID identifier, final Organization organization) {
        LOGGER.debug()
                .setMessage("Getting pagerduty endpoint")
                .addData("identifier", identifier)
                .addData("organization", organization)
                .log();

        final models.ebean.PagerDutyEndpoint pagerDutyEndpoint = Ebean.find(models.ebean.PagerDutyEndpoint.class)
                .where()
                .eq("uuid", identifier)
                .eq("organization.uuid", organization.getId())
                .findOne();
        if (pagerDutyEndpoint == null) {
            return Optional.empty();
        }
        return Optional.of(pagerDutyEndpoint.toInternal());
    }

    @Override
    public Optional<PagerDutyEndpoint> getByName(final String name, final Organization organization) {
        LOGGER.debug()
                .setMessage("Getting pagerduty endpoint")
                .addData("name", name)
                .addData("organization", organization)
                .log();

        final models.ebean.PagerDutyEndpoint pagerDutyEndpoint = Ebean.find(models.ebean.PagerDutyEndpoint.class)
                .where()
                .eq("name", name)
                .eq("organization.uuid", organization.getId())
                .findOne();
        if (pagerDutyEndpoint == null) {
            return Optional.empty();
        }
        return Optional.of(pagerDutyEndpoint.toInternal());
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
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Querying")
                .addData("query", query)
                .log();

        // Create the base query
        final PagedList<models.ebean.PagerDutyEndpoint> pagedPagerDutyEndpoints = _pagerDutyEndpointQueryGenerator.createPagerDutyEndpointQuery(query);

        final List<PagerDutyEndpoint> values = new ArrayList<>();
        pagedPagerDutyEndpoints.getList().forEach(ebeanPagerDutyEndpoint -> values.add(ebeanPagerDutyEndpoint.toInternal()));

        // Transform the results
        return new DefaultQueryResult<>(values, pagedPagerDutyEndpoints.getTotalCount());
    }

    @Override
    public void upsert(PagerDutyEndpoint pagerDutyEndpoint, Organization organization) {
        LOGGER.debug()
                .setMessage("Upserting pagerduty endpoint")
                .addData("endpoint", pagerDutyEndpoint)
                .addData("organization", organization)
                .log();

        try (Transaction transaction = Ebean.beginTransaction()) {
            models.ebean.PagerDutyEndpoint ebeanPagerDutyEndpoint = Ebean.find(models.ebean.PagerDutyEndpoint.class)
                    .where()
                    .eq("uuid", pagerDutyEndpoint.getId())
                    .eq("organization.uuid", organization.getId())
                    .findOne();
            boolean isCreated = false;
            if (ebeanPagerDutyEndpoint == null) {
                ebeanPagerDutyEndpoint = new models.ebean.PagerDutyEndpoint();
                isCreated = true;
            }

            ebeanPagerDutyEndpoint.setUuid(pagerDutyEndpoint.getId());
            ebeanPagerDutyEndpoint.setName(pagerDutyEndpoint.getName());
            ebeanPagerDutyEndpoint.setPagerDutyUrl(pagerDutyEndpoint.getPagerDutyUrl());
            ebeanPagerDutyEndpoint.setServiceKey(pagerDutyEndpoint.getServiceKey());
            ebeanPagerDutyEndpoint.setComment(pagerDutyEndpoint.getComment());
            ebeanPagerDutyEndpoint.setOrganization(models.ebean.Organization.findByOrganization(organization));
            _pagerDutyEndpointQueryGenerator.savePagerDutyEndpoint(ebeanPagerDutyEndpoint);
            transaction.commit();

            LOGGER.info()
                    .setMessage("Upserted pagerduty endpoint")
                    .addData("endpoint", pagerDutyEndpoint)
                    .addData("isCreated", isCreated)
                    .log();
            // CHECKSTYLE.OFF: IllegalCatchCheck
        } catch (final RuntimeException e) {
            // CHECKSTYLE.ON: IllegalCatchCheck
            LOGGER.error()
                    .setMessage("Failed to upsert pagerduty endpoint")
                    .addData("endpoint", pagerDutyEndpoint)
                    .setThrowable(e)
                    .log();
            throw new PersistenceException(e);
        }
    }

    @Override
    public int delete(final UUID identifier, final Organization organization) {
        LOGGER.debug()
                .setMessage("Deleting pagerduty endpoint")
                .addData("identifier", identifier)
                .addData("organization", organization)
                .log();
        return Ebean.find(models.ebean.PagerDutyEndpoint.class)
                .where()
                .eq("uuid", identifier)
                .eq("organization.uuid", organization.getId())
                .delete();
    }

    private void assertIsOpen() {
        assertIsOpen(true);
    }

    private void assertIsOpen(final boolean expectedState) {
        if (_isOpen.get() != expectedState) {
            throw new IllegalStateException(String.format("PagerDutyEndpointRepository repository is not %s", expectedState ? "open" : "closed"));
        }
    }

    private final AtomicBoolean _isOpen = new AtomicBoolean(false);
    private final PagerDutyEndpointQueryGenerator _pagerDutyEndpointQueryGenerator;

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePagerDutyEndpointRepository.class);

    /**
     * Interface for database query generation.
     */
    public interface PagerDutyEndpointQueryGenerator {

        /**
         * Translate the <code>NotificationQuery</code> to an Ebean <code>Query</code>.
         *
         * @param query The repository agnostic {@link NotificationGroupQuery}.
         * @return The database specific {@link PagedList} query result.
         */
        PagedList<models.ebean.PagerDutyEndpoint> createPagerDutyEndpointQuery(PagerDutyEndpointQuery query);

        /**
         * Save the {@link models.ebean.PagerDutyEndpoint} to the database. This needs to be executed in a transaction.
         *
         * @param pagerDutyEndpoint The {@link models.ebean.PagerDutyEndpoint} model instance to save.
         */
        void savePagerDutyEndpoint(models.ebean.PagerDutyEndpoint pagerDutyEndpoint);
    }

    /**
     * RDBMS agnostic query for pagerduty endpoints.
     */
    public static final class GenericQueryGenerator implements PagerDutyEndpointQueryGenerator {

        @Override
        public PagedList<models.ebean.PagerDutyEndpoint> createPagerDutyEndpointQuery(final PagerDutyEndpointQuery query) {
            ExpressionList<models.ebean.PagerDutyEndpoint> ebeanExpressionList = Ebean.find(models.ebean.PagerDutyEndpoint.class).where();
            ebeanExpressionList = ebeanExpressionList.eq("organization.uuid", query.getOrganization().getId());

            //TODO(deepika): Add full text search [ISSUE-11]
            if (query.getContains().isPresent()) {
                final Junction<models.ebean.PagerDutyEndpoint> junction = ebeanExpressionList.disjunction();
                ebeanExpressionList = junction.ilike("name", "%" + query.getContains().get() + "%");
                ebeanExpressionList = ebeanExpressionList.endJunction();
            }
            final Query<models.ebean.PagerDutyEndpoint> ebeanQuery = ebeanExpressionList.query();
            int offset = 0;
            if (query.getOffset().isPresent()) {
                offset = query.getOffset().get();
            }
            return ebeanQuery.setFirstRow(offset).setMaxRows(query.getLimit()).findPagedList();
        }

        @Override
        public void savePagerDutyEndpoint(final models.ebean.PagerDutyEndpoint pagerDutyEndpoint) {
            Ebean.save(pagerDutyEndpoint);
        }
    }
}
