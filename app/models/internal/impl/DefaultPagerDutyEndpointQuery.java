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
package models.internal.impl;

import com.arpnetworking.logback.annotations.Loggable;
import com.arpnetworking.metrics.portal.pagerduty.PagerDutyEndpointRepository;
import com.google.common.base.MoreObjects;
import models.internal.PagerDutyEndpoint;
import models.internal.PagerDutyEndpointQuery;
import models.internal.Organization;
import models.internal.QueryResult;

import java.util.Optional;

/**
 * Default internal model implementation for an pagerduty endpoint query.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
@Loggable
public final class DefaultPagerDutyEndpointQuery implements PagerDutyEndpointQuery {

    /**
     * Public constructor.
     *
     * @param repository The <code>AlertRepository</code>
     * @param organization The <code>Organization</code> to search in
     */
    public DefaultPagerDutyEndpointQuery(final PagerDutyEndpointRepository repository, final Organization organization) {
        _repository = repository;
        _organization = organization;
    }

    @Override
    public PagerDutyEndpointQuery contains(final Optional<String> contains) {
        _contains = contains;
        return this;
    }

    @Override
    public PagerDutyEndpointQuery limit(final int limit) {
        _limit = limit;
        return this;
    }

    @Override
    public PagerDutyEndpointQuery offset(final Optional<Integer> offset) {
        _offset = offset;
        return this;
    }

    @Override
    public QueryResult<PagerDutyEndpoint> execute() {
        return _repository.query(this);
    }

    @Override
    public Organization getOrganization() {
        return _organization;
    }

    @Override
    public Optional<String> getContains() {
        return _contains;
    }

    @Override
    public int getLimit() {
        return _limit;
    }

    @Override
    public Optional<Integer> getOffset() {
        return _offset;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", Integer.toHexString(System.identityHashCode(this)))
                .add("class", this.getClass())
                .add("Repository", _repository)
                .add("Contains", _contains)
                .add("Limit", _limit)
                .add("Offset", _offset)
                .toString();
    }

    private final PagerDutyEndpointRepository _repository;
    private final Organization _organization;
    private Optional<String> _contains = Optional.empty();
    private int _limit = DEFAULT_LIMIT;
    private Optional<Integer> _offset = Optional.empty();

    private static final int DEFAULT_LIMIT = 1000;
}
