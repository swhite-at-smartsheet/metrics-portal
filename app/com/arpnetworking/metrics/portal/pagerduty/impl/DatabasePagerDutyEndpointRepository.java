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
package com.arpnetworking.metrics.portal.pagerduty.impl;

import com.arpnetworking.metrics.portal.pagerduty.PagerDutyEndpointRepository;
import com.arpnetworking.play.configuration.ConfigurationHelper;
import com.arpnetworking.steno.Logger;
import com.arpnetworking.steno.LoggerFactory;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import io.ebean.*;
import models.internal.Organization;
import models.internal.PagerDutyEndpoint;
import models.internal.PagerDutyEndpointQuery;
import models.internal.QueryResult;
import models.internal.impl.DefaultPagerDutyEndpointQuery;
import models.internal.impl.DefaultQueryResult;
import play.Environment;
import play.db.ebean.EbeanDynamicEvolutions;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Implementation of <code>PagerDutyEndpointRepository</code> using SQL database.
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
     * @param pagerDutyEndpointQueryGenerator Instance of <code>PagerDutyEndpointQueryGenerator</code>.
     */
    public DatabasePagerDutyEndpointRepository(final DatabasePagerDutyEndpointRepository.PagerDutyEndpointQueryGenerator pagerDutyEndpointQueryGenerator) {
        _pagerDutyEndpointQueryGenerator = pagerDutyEndpointQueryGenerator;
    }

    @Override
    public void open() {
        assertIsOpen(false);
        LOGGER.debug().setMessage("Opening pagerDutyEndpoint repository").log();
        _isOpen.set(true);
    }

    @Override
    public void close() {
        assertIsOpen();
        LOGGER.debug().setMessage("Closing pagerDutyEndpoint repository").log();
        _isOpen.set(false);
    }

    @Override
    public void addOrUpdatePagerDutyEndpoint(final PagerDutyEndpoint pagerDutyEndpoint) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Adding or updating pagerDutyEndpoint")
                .addData("pagerDutyEndpoint", pagerDutyEndpoint)
                .log();

        final Transaction transaction = Ebean.beginTransaction();
        try {
            models.ebean.PagerDutyEndpoint ebeanPagerDutyEndpoint = Ebean.find(models.ebean.PagerDutyEndpoint.class)
                    .where()
                    .eq("name", pagerDutyEndpoint.getName())
                    .findOne();
            boolean isNewPagerDutyEndpoint = false;
            if (ebeanPagerDutyEndpoint == null) {
                ebeanPagerDutyEndpoint = new models.ebean.PagerDutyEndpoint();
                isNewPagerDutyEndpoint = true;
            }
            ebeanPagerDutyEndpoint.setName(pagerDutyEndpoint.getName());
            ebeanPagerDutyEndpoint.setAddress(pagerDutyEndpoint.getAddress());
            ebeanPagerDutyEndpoint.setServiceKey(pagerDutyEndpoint.getServiceKey());
            _pagerDutyEndpointQueryGenerator.savePagerDutyEndpoint(ebeanPagerDutyEndpoint);
            transaction.commit();

            LOGGER.info()
                    .setMessage("Upserted pagerDutyEndpoint")
                    .addData("pagerDutyEndpoint", pagerDutyEndpoint)
                    .addData("isCreated", isNewPagerDutyEndpoint)
                    .log();
        } finally {
            transaction.end();
        }
    }

    @Override
    public void deletePagerDutyEndpoint(final String name) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Deleting pagerDutyEndpoint")
                .addData("name", name)
                .log();
        final models.ebean.PagerDutyEndpoint ebeanPagerDutyEndpoint = Ebean.find(models.ebean.PagerDutyEndpoint.class)
                .where()
                .eq("name", name)
                .findOne();
        if (ebeanPagerDutyEndpoint != null) {
            Ebean.delete(ebeanPagerDutyEndpoint);
            LOGGER.info()
                    .setMessage("Deleted pagerDutyEndpoint")
                    .addData("name", name)
                    .log();
        } else {
            LOGGER.info()
                    .setMessage("PagerDutyEndpoint not found")
                    .addData("name", name)
                    .log();
        }
    }

    @Override
    public PagerDutyEndpointQuery createQuery() {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Preparing query")
                .log();
        return new DefaultPagerDutyEndpointQuery(this);
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

        // Compute the etag
        // NOTE: Another way to do this would be to use the version field and hash those together.
        final String etag = Long.toHexString(pagedPagerDutyEndpoints.getList().stream()
                .map(pagerDutyEndpoint -> pagerDutyEndpoint.getUpdatedAt().after(pagerDutyEndpoint.getCreatedAt()) ? pagerDutyEndpoint.getUpdatedAt() : pagerDutyEndpoint.getCreatedAt())
                .max(Timestamp::compareTo)
                .orElse(new Timestamp(0))
                .getTime());

        // Transform the results
        return new DefaultQueryResult<>(
                pagedPagerDutyEndpoints.getList()
                        .stream()
                        .map(models.ebean.PagerDutyEndpoint::toInternal)
                        .collect(Collectors.toList()),
                pagedPagerDutyEndpoints.getTotalCount(),
                etag);
    }

//    @Override
//    public long getPagerDutyEndpointCount(final Organization organization) {
//        assertIsOpen();
//        return Ebean.find(models.ebean.PagerDutyEndpoint.class)
//                .where()
//                .eq("organization.uuid", organization.getId())
//                .findCount();
//    }

//    @Override
//    public long getPagerDutyEndpointCount(final MetricsSoftwareState metricsSoftwareState, final Organization organization) {
//        assertIsOpen();
//        return Ebean.find(models.ebean.PagerDutyEndpoint.class)
//                .where()
//                .eq("organization.uuid", organization.getId())
//                .eq("metrics_software_state", metricsSoftwareState.toString())
//                .findCount();
//    }

    private static String mapField(final PagerDutyEndpointQuery.Field field) {
        switch (field) {
            case NAME:
                return "name";
            case ADDRESS:
                return "address";
            case SERVICEKEY:
                return "service_key";
            default:
                throw new UnsupportedOperationException(String.format("Unrecognized field; field=%s", field));
        }
    }

    private void assertIsOpen() {
        assertIsOpen(true);
    }

    private void assertIsOpen(final boolean expectedState) {
        if (_isOpen.get() != expectedState) {
            throw new IllegalStateException(String.format("PagerDutyEndpoint repository is not %s", expectedState ? "open" : "closed"));
        }
    }

    private final AtomicBoolean _isOpen = new AtomicBoolean(false);
    private final DatabasePagerDutyEndpointRepository.PagerDutyEndpointQueryGenerator _pagerDutyEndpointQueryGenerator;

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePagerDutyEndpointRepository.class);

    /**
     * Inteface for database query generation.
     */
    public interface PagerDutyEndpointQueryGenerator {

        /**
         * Translate the <code>PagerDutyEndpointQuery</code> to an Ebean <code>Query</code>.
         *
         * @param query The repository agnostic <code>PagerDutyEndpointQuery</code>.
         * @param organization The organization to query in.
         * @return The database specific <code>PagedList</code> query result.
         */
        PagedList<models.ebean.PagerDutyEndpoint> createPagerDutyEndpointQuery(PagerDutyEndpointQuery query, Organization organization);

        /**
         * Save the <code>PagerDutyEndpoint</code> to the database. This needs to be executed in a transaction.
         *
         * @param pagerDutyEndpoint The <code>PagerDutyEndpoint</code> model instance to save.
         */
        void savePagerDutyEndpoint(models.ebean.PagerDutyEndpoint pagerDutyEndpoint);
    }

    /**
     * RDBMS agnostic query for pagerDutyEndpoints using 'like'.
     */
    public static final class GenericQueryGenerator implements DatabasePagerDutyEndpointRepository.PagerDutyEndpointQueryGenerator {

        @Override
        public PagedList<models.ebean.PagerDutyEndpoint> createPagerDutyEndpointQuery(final PagerDutyEndpointQuery query) {
            ExpressionList<models.ebean.PagerDutyEndpoint> ebeanExpressionList = Ebean.find(models.ebean.PagerDutyEndpoint.class).where();
            //ebeanExpressionList = ebeanExpressionList.eq("organization.uuid", organization.getId());

            Query<models.ebean.PagerDutyEndpoint> ebeanQuery = ebeanExpressionList.query();
            if (query.getSortBy().isPresent()) {
                ebeanQuery = ebeanQuery.orderBy().asc(mapField(query.getSortBy().get()));
            }

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

//    /**
//     * Postgresql specific full text index to query for pagerDutyEndpoints.
//     */
//    public static final class PostgresqlPagerDutyEndpointQueryGenerator implements DatabasePagerDutyEndpointRepository.PagerDutyEndpointQueryGenerator {
//
//        @Override
//        public PagedList<models.ebean.PagerDutyEndpoint> createPagerDutyEndpointQuery(final PagerDutyEndpointQuery query, final Organization organization) {
//            final StringBuilder selectBuilder = new StringBuilder(
//                    "select t0.id, t0.version, t0.created_at, t0.updated_at, "
//                            + "t0.name, t0.cluster, t0.metrics_software_state "
//                            + "from portal.pagerDutyEndpoints t0");
//            final StringBuilder whereBuilder = new StringBuilder();
//            final StringBuilder orderBuilder = new StringBuilder();
//            final Map<String, Object> parameters = Maps.newHashMap();
//
//            // Add the partial pagerDutyEndpoint name clause using the postgresql full text index
//            if (query.getPartialPagerDutyEndpointname().isPresent() && !query.getPartialPagerDutyEndpointname().get().isEmpty()) {
//                final List<String> queryTokens = Arrays.asList(query.getPartialPagerDutyEndpointname().get().split(" "));
//                final String prefixExpression = queryTokens
//                        .stream()
//                        .map(s -> s + ":*")
//                        .reduce((s1, s2) -> s1 + " & " + s2)
//                        .orElse(null);
//                final String termExpression = queryTokens
//                        .stream()
//                        .reduce((s1, s2) -> s1 + " & " + s2)
//                        .orElse(null);
//                if (prefixExpression != null && termExpression != null) {
//                    parameters.put("prefixQuery", prefixExpression);
//                    parameters.put("termQuery", termExpression);
//                    selectBuilder.append(", to_tsquery('simple',:prefixQuery) prefixQuery, to_tsquery('simple',:termQuery) termQuery");
//                    whereBuilder.append("where (t0.name_idx_col @@ prefixQuery or t0.name_idx_col @@ termQuery)");
//                    orderBuilder.append("order by ts_rank(t0.name_idx_col, prefixQuery) * ts_rank(t0.name_idx_col, termQuery) "
//                            + "/ char_length(t0.name) DESC, name ASC");
//                } else {
//                    // The user enters only removable tokens (e.g. space, period, etc.)
//                    LOGGER.debug()
//                            .setMessage("Skipping partial pagerDutyEndpoint name query clause")
//                            .addData("organization", organization)
//                            .addData("partialPagerDutyEndpointName", query.getPartialPagerDutyEndpointname().get())
//                            .addData("prefixExpression", prefixExpression)
//                            .addData("termExpression", termExpression)
//                            .log();
//                }
//            }
//
//            // Add the sort order
//            if (query.getSortBy().isPresent()) {
//                // NOTE: Replace the ordering (if any) with the user specified one
//                orderBuilder.setLength(0);
//                orderBuilder.append("order by ")
//                        .append(mapField(query.getSortBy().get()))
//                        .append(" ASC");
//            }
//
//            // Compute the page offset
//            int offset = 0;
//            if (query.getOffset().isPresent()) {
//                offset = query.getOffset().get();
//            }
//
//            // Create and execute the raw parameterized query
//            return createParameterizedPagerDutyEndpointQueryFromRawSql(
//                    selectBuilder.toString() + " " + whereBuilder.toString() + " " + orderBuilder.toString(),
//                    parameters)
//                    .setFirstRow(offset)
//                    .setMaxRows(query.getLimit())
//                    .findPagedList();
//        }
//
//        @Override
//        public void savePagerDutyEndpoint(final models.ebean.PagerDutyEndpoint pagerDutyEndpoint) {
//            final String pagerDutyEndpointname = pagerDutyEndpoint.getName();
//            final String labels = pagerDutyEndpointname.replace('.', ' ');
//            final String words = labels.replace('-', ' ');
//            final String alnum = tokenize(labels)
//                    .stream()
//                    .reduce((s1, s2) -> s1 + " " + s2)
//                    .orElse("");
//
//            Ebean.save(pagerDutyEndpoint);
//            Ebean.createSqlUpdate(
//                    "UPDATE portal.pagerDutyEndpoints SET name_idx_col = "
//                            + "setweight(to_tsvector('simple', coalesce(:pagerDutyEndpointname,'')), 'A')"
//                            + "|| setweight(to_tsvector('simple', coalesce(:labels,'')), 'B')"
//                            + "|| setweight(to_tsvector('simple', coalesce(:words,'')), 'C')"
//                            + "|| setweight(to_tsvector('simple', coalesce(:alnum,'')), 'D')"
//                            + "WHERE id = :id")
//                    .setParameter("id", pagerDutyEndpoint.getId())
//                    .setParameter("pagerDutyEndpointname", pagerDutyEndpointname)
//                    .setParameter("labels", labels)
//                    .setParameter("words", words)
//                    .setParameter("alnum", alnum)
//                    .execute();
//        }
//
//        // NOTE: Package private for testing
//        /* package private */ static List<String> tokenize(final String word) {
//            final List<String> tokens = new ArrayList<>();
//            for (final String token : word.split("([^\\p{Alnum}])|((?<=\\p{Alpha})(?=\\p{Digit})|(?<=\\p{Digit})(?=\\p{Alpha}))")) {
//                if (!token.isEmpty()) {
//                    tokens.add(token.toLowerCase(Locale.getDefault()));
//                }
//            }
//            return tokens;
//        }
//
//        private static Query<models.ebean.PagerDutyEndpoint> createParameterizedPagerDutyEndpointQueryFromRawSql(
//                final String sql,
//                final Map<String, Object> parameters) {
//            final RawSql rawSql = RawSqlBuilder.parse(sql)
//                    .columnMapping("t0.id", "id")
//                    .columnMapping("t0.version", "version")
//                    .columnMapping("t0.created_at", "createdAt")
//                    .columnMapping("t0.updated_at", "updatedAt")
//                    .columnMapping("t0.name", "name")
//                    .columnMapping("t0.cluster", "cluster")
//                    .columnMapping("t0.metrics_software_state", "metricsSoftwareState")
//                    .create();
//            final Query<models.ebean.PagerDutyEndpoint> ebeanQuery = Ebean.find(models.ebean.PagerDutyEndpoint.class).setRawSql(rawSql);
//            for (final Map.Entry<String, Object> parameter : parameters.entrySet()) {
//                ebeanQuery.setParameter(parameter.getKey(), parameter.getValue());
//            }
//            return ebeanQuery;
//        }
//
//        private static void beginOrExtend(final StringBuilder stringBuilder, final String beginning, final String continuation) {
//            if (stringBuilder.length() == 0) {
//                stringBuilder.append(beginning);
//            } else {
//                stringBuilder.append(continuation);
//            }
//        }
//    }
}
