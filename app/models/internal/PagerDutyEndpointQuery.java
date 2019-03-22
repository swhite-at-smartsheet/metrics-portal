/*
 * Copyright 2014 Groupon.com
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
 * Internal model interface for a host query.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public interface PagerDutyEndpointQuery {

    /**
     * The maximum number of hosts to return. Optional. Default is 1000.
     *
     * @param limit The maximum number of hosts to return.
     * @return This instance of <code>HostQuery</code>.
     */
    PagerDutyEndpointQuery limit(int limit);

    /**
     * The offset into the result set. Optional. Default is not set.
     *
     * @param offset The offset into the result set.
     * @return This instance of <code>HostQuery</code>.
     */
    PagerDutyEndpointQuery offset(Optional<Integer> offset);

    /**
     * Sort the results by the specified field. Optional. Default sorting is defined by the underlying repository
     * implementation but it is strongly recommended that the repository make some attempt to sort by score or relevance
     * given the inputs.
     *
     * @param field The <code>Field</code> to sort on.
     * @return This instance of <code>HostQuery</code>.
     */
    PagerDutyEndpointQuery sortBy(Optional<Field> field);

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

    /**
     * Accessor for the field to sort by.
     * @return The field to sort by.
     */
    Optional<Field> getSortBy();

    /**
     * The fields defined for a host.
     */
    enum Field {
        NAME,
        ADDRESS,
        SERVICEKEY
    }
}
