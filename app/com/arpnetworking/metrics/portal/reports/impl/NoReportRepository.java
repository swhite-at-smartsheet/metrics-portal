/*
 * Copyright 2019 Dropbox, Inc.
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

package com.arpnetworking.metrics.portal.reports.impl;

import com.arpnetworking.metrics.portal.reports.ReportQuery;
import com.arpnetworking.metrics.portal.reports.ReportRepository;
import com.arpnetworking.metrics.portal.scheduling.JobQuery;
import com.arpnetworking.steno.Logger;
import com.arpnetworking.steno.LoggerFactory;
import com.google.common.collect.ImmutableList;
import models.internal.Organization;
import models.internal.QueryResult;
import models.internal.impl.DefaultJobQuery;
import models.internal.impl.DefaultQueryResult;
import models.internal.reports.Report;
import models.internal.scheduling.Job;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * An empty {@code ReportRepository}.
 *
 * @author Christian Briones (cbriones at dropbox dot com).
 */
public final class NoReportRepository implements ReportRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoReportRepository.class);
    private final AtomicBoolean _isOpen = new AtomicBoolean(false);

    /**
     * Default constructor.
     */
    public NoReportRepository() {}

    @Override
    public Optional<Report> getReport(final UUID identifier, final Organization organization) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Getting report")
                .addData("uuid", identifier)
                .addData("organization.uuid", organization.getId())
                .log();
        return Optional.empty();
    }

    @Override
    public void addOrUpdateReport(final Report report, final Organization organization) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Upserting report")
                .addData("report", report)
                .addData("organization.uuid", organization.getId())
                .log();
    }

    @Override
    public void open() {
        assertIsOpen(false);
        LOGGER.debug().setMessage("Opening NoReportRepository").log();
        _isOpen.set(true);
    }

    @Override
    public void close() {
        assertIsOpen();
        LOGGER.debug().setMessage("Closing NoReportRepository").log();
        _isOpen.set(false);
    }

    @Override
    public Optional<Job<Report.Result>> getJob(final UUID id, final Organization organization) {
        return getReport(id, organization).map(Function.identity());
    }

    @Override
    public Optional<Instant> getLastRun(final UUID id, final Organization organization) throws NoSuchElementException {
        assertIsOpen();
        return Optional.empty();
    }

    @Override
    public void jobStarted(final UUID id, final Organization organization, final Instant scheduled) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Report job started")
                .addData("report.uuid", id)
                .addData("organization", organization)
                .addData("scheduled", scheduled)
                .log();
    }

    @Override
    public void jobSucceeded(final UUID id, final Organization organization, final Instant scheduled, final Report.Result result) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Report job succeeded")
                .addData("report.uuid", id)
                .addData("organization", organization)
                .addData("scheduled", scheduled)
                .addData("result", result)
                .log();
    }

    @Override
    public void jobFailed(final UUID id, final Organization organization, final Instant scheduled, final Throwable error) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Report job failed")
                .addData("report.uuid", id)
                .addData("organization", organization)
                .addData("scheduled", scheduled)
                .addData("error", error)
                .log();
    }

    @Override
    public JobQuery<Report.Result> createQuery(final Organization organization) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Preparing job query")
                .addData("organization", organization)
                .log();
        return new DefaultJobQuery<>(this, organization);
    }

    @Override
    public QueryResult<Report> query(final ReportQuery query) {
        assertIsOpen();
        LOGGER.debug()
                .setMessage("Executing query")
                .addData("query", query)
                .log();
        return new DefaultQueryResult<>(ImmutableList.of(), 0);
    }

    private void assertIsOpen() {
        assertIsOpen(true);
    }

    private void assertIsOpen(final boolean expectedState) {
        if (_isOpen.get() != expectedState) {
            throw new IllegalStateException(String.format("Report repository is not %s", expectedState ? "open" : "closed"));
        }
    }
}
