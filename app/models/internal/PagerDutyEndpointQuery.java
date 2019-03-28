/**
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
package models.internal;

import java.util.Optional;

/**
 * Internal model interface for a pagerduty endpoint query.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public interface PagerDutyEndpointQuery {

    /**
     * Set the text to query for. Optional. Defaults to no text.
     *
     * @param contains The text to match.
     * @return This instance of <code>PagerDutyEndpointQuery</code>.
     */
    PagerDutyEndpointQuery contains(Optional<String> contains);

    /**
     * The maximum number of alerts to return.  Optional. Default is 1000.
     *
     * @param limit The maximum number of endpoints to return.
     * @return This instance of <code>PagerDutyEndpointQuery</code>.
     */
    PagerDutyEndpointQuery limit(int limit);

    /**
     * The offset into the result set. Optional. Default is not set.
     *
     * @param offset The offset into the result set.
     * @return This instance of <code>PagerDutyEndpointQuery</code>.
     */
    PagerDutyEndpointQuery offset(Optional<Integer> offset);

    /**
     * Execute the query and return the results.
     *
     * @return The results of the query as an {@code QueryResult<PagerDutyEndpoint>} instance.
     */
    QueryResult<PagerDutyEndpoint> execute();

    /**
     * Accessor for the organization.
     *
     * @return The organization.
     */
    Organization getOrganization();

    /**
     * Accessor for the contains.
     *
     * @return The contains.
     */
    Optional<String> getContains();

    /**
     * Accessor for the limit.
     *
     * @return The limit.
     */
    int getLimit();

    /**
     * Accessor for the offset.
     *
     * @return The offset.
     */
    Optional<Integer> getOffset();
}