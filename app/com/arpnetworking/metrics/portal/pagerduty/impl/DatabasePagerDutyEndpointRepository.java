/**
 * Copyright 2017 Smartsheet
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
import models.internal.NotificationGroupQuery;
import models.internal.PagerDutyEndpoint;
import play.Environment;
import play.db.ebean.EbeanDynamicEvolutions;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
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
            final EbeanDynamicEvolutions ignored) {
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }


    @Override
    public Optional<PagerDutyEndpoint> get(final UUID identifier) {
        LOGGER.debug()
                .setMessage("Getting pagerduty endpoint")
                .addData("identifier", identifier)
                .log();

        final models.ebean.PagerDutyEndpoint pagerDutyEndpoint = Ebean.find(models.ebean.PagerDutyEndpoint.class)
                .where()
                .eq("uuid", identifier)
                .findOne();
        if (pagerDutyEndpoint == null) {
            return Optional.empty();
        }
        return Optional.of(pagerDutyEndpoint.toInternal());
    }

    @Override
    public void upsert(PagerDutyEndpoint pagerDutyEndpoint) {
        LOGGER.debug()
                .setMessage("Upserting pagerduty endpoint")
                .addData("endpoint", pagerDutyEndpoint)
                .log();

        try (Transaction transaction = Ebean.beginTransaction()) {
            models.ebean.PagerDutyEndpoint ebeanPagerDutyEndpoint = Ebean.find(models.ebean.PagerDutyEndpoint.class)
                    .where()
                    .eq("uuid", pagerDutyEndpoint.getUuid())
                    .findOne();
            boolean isCreated = false;
            if (ebeanPagerDutyEndpoint == null) {
                ebeanPagerDutyEndpoint = new models.ebean.PagerDutyEndpoint();
                isCreated = true;
            }

            ebeanPagerDutyEndpoint.setUuid(pagerDutyEndpoint.getUuid());
            ebeanPagerDutyEndpoint.setName(pagerDutyEndpoint.getName());
            ebeanPagerDutyEndpoint.setAddress(pagerDutyEndpoint.getAddress());
            ebeanPagerDutyEndpoint.setServiceKey(pagerDutyEndpoint.getServiceKey());
            ebeanPagerDutyEndpoint.setComment(pagerDutyEndpoint.getComment());
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
    public int delete(final UUID identifier) {
        LOGGER.debug()
                .setMessage("Deleting pagerduty endpoint")
                .addData("id", identifier)
                .log();
        return Ebean.find(models.ebean.PagerDutyEndpoint.class)
                .where()
                .eq("uuid", identifier)
                .delete();
    }

    private final AtomicBoolean _isOpen = new AtomicBoolean(false);

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePagerDutyEndpointRepository.class);

    /**
     * Inteface for database query generation.
     */
    public interface NotificationQueryGenerator {

        /**
         * Translate the <code>NotificationQuery</code> to an Ebean <code>Query</code>.
         *
         * @param query The repository agnostic {@link NotificationGroupQuery}.
         * @return The database specific {@link PagedList} query result.
         */
        PagedList<models.ebean.NotificationGroup> createNotificationGroupQuery(NotificationGroupQuery query);

        /**
         * Save the {@link models.ebean.NotificationGroup} to the database. This needs to be executed in a transaction.
         *
         * @param notificationGroup The {@link models.ebean.NotificationGroup} model instance to save.
         */
        void saveNotificationGroup(models.ebean.NotificationGroup notificationGroup);
    }

    /**
     * RDBMS agnostic query for notification groups.
     */
    public static final class GenericQueryGenerator implements NotificationQueryGenerator {

        @Override
        public PagedList<models.ebean.NotificationGroup> createNotificationGroupQuery(final NotificationGroupQuery query) {
            ExpressionList<models.ebean.NotificationGroup> ebeanExpressionList = Ebean.find(models.ebean.NotificationGroup.class).where();
            ebeanExpressionList = ebeanExpressionList.eq("organization.uuid", query.getOrganization().getId());

            //TODO(deepika): Add full text search [ISSUE-11]
            if (query.getContains().isPresent()) {
                final Junction<models.ebean.NotificationGroup> junction = ebeanExpressionList.disjunction();
                ebeanExpressionList = junction.ilike("name", "%" + query.getContains().get() + "%");
                ebeanExpressionList = ebeanExpressionList.endJunction();
            }
            final Query<models.ebean.NotificationGroup> ebeanQuery = ebeanExpressionList.query();
            int offset = 0;
            if (query.getOffset().isPresent()) {
                offset = query.getOffset().get();
            }
            return ebeanQuery.setFirstRow(offset).setMaxRows(query.getLimit()).findPagedList();
        }

        @Override
        public void saveNotificationGroup(final models.ebean.NotificationGroup notificationGroup) {
            Ebean.save(notificationGroup);
        }
    }
}
