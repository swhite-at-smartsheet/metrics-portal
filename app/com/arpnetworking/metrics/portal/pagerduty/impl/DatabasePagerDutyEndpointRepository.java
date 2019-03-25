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
import com.arpnetworking.metrics.portal.pagerduty.notifications.NotificationRepository;
import com.arpnetworking.play.configuration.ConfigurationHelper;
import com.arpnetworking.steno.Logger;
import com.arpnetworking.steno.LoggerFactory;
import com.typesafe.config.Config;
import io.ebean.*;
import models.ebean.NotificationRecipient;
import models.internal.*;
import models.internal.impl.DefaultNotificationGroupQuery;
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
 * Repository for storing notification data in a database.
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
            final EbeanDynamicEvolutions ignored)
            throws Exception {
        this(ConfigurationHelper.<NotificationQueryGenerator>getType(
                        environment,
                        config,
                        "notificationRepository.notificationQueryGenerator.type")
                .getDeclaredConstructor()
                .newInstance());

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
    }



    @Override
    void addOrUpdatePagerDutyEndpoint(PagerDutyEndpoint pagerDutyEndpoint) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Upserting notification group")
                .addData("group", group)
                .addData("organization", organization)
                .log();


        try (Transaction transaction = Ebean.beginTransaction()) {
            models.ebean.NotificationGroup notificationGroup = Ebean.find(models.ebean.NotificationGroup.class)
                    .where()
                    .eq("uuid", group.getId())
                    .eq("organization.uuid", organization.getId())
                    .findOne();
            boolean isNewGroup = false;
            if (notificationGroup == null) {
                notificationGroup = new models.ebean.NotificationGroup();
                isNewGroup = true;
            }

            notificationGroup.setOrganization(models.ebean.Organization.findByOrganization(organization));
            notificationGroup.setUuid(group.getId());
            notificationGroup.setName(group.getName());
            _notificationQueryGenerator.saveNotificationGroup(notificationGroup);
            transaction.commit();

            LOGGER.info()
                    .setMessage("Upserted notification group")
                    .addData("group", group)
                    .addData("organization", organization)
                    .addData("isCreated", isNewGroup)
                    .log();
            // CHECKSTYLE.OFF: IllegalCatchCheck
        } catch (final RuntimeException e) {
            // CHECKSTYLE.ON: IllegalCatchCheck
            LOGGER.error()
                    .setMessage("Failed to upsert notification group")
                    .addData("group", group)
                    .addData("organization", organization)
                    .setThrowable(e)
                    .log();
            throw new PersistenceException(e);
        }
    }


    private void assertIsOpen() {
        assertIsOpen(true);
    }

    private void assertIsOpen(final boolean expectedState) {
        if (_isOpen.get() != expectedState) {
            throw new IllegalStateException(String.format("Notification repository is not %s", expectedState ? "open" : "closed"));
        }
    }


    private DatabasePagerDutyEndpointRepository(final NotificationQueryGenerator queryGenerator) {
        _notificationQueryGenerator = queryGenerator;
    }

    private final AtomicBoolean _isOpen = new AtomicBoolean(false);
    private final NotificationQueryGenerator _notificationQueryGenerator;

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