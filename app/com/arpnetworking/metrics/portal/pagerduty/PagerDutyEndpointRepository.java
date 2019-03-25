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
package com.arpnetworking.metrics.portal.pagerduty;

import models.internal.*;

import java.util.Optional;
import java.util.UUID;

/**
 * A repository to store pagerduty endpoints.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public interface PagerDutyEndpointRepository extends AutoCloseable {
    /**
     * Open the {@link PagerDutyEndpointRepository}.
     */
    void open();

    /**
     * Close the {@link PagerDutyEndpointRepository}.
     */
    void close();

    /**
     * Get the {@link PagerDutyEndpoint} by identifier.
     *
     * @param identifier The {@link PagerDutyEndpoint} identifier.
     * @return The matching {@link PagerDutyEndpoint} if found or <code>Optional.empty()</code>.
     */
    Optional<PagerDutyEndpoint> get(UUID identifier);

    /**
     * Delete an <code>Alert</code> by identifier.
     *
     * @param identifier The <code>PagerDutyEndpoint</code> identifier.
     * @return 1 if it was deleted, 0 otherwise.
     */
    int delete(UUID identifier);

    /**
     * Add a new endpoint or update an existing endpoint in the repository.
     *
     * @param pagerDutyEndpoint The PagerDutyEndpoint to add to the repository.
     */
    void upsert(PagerDutyEndpoint pagerDutyEndpoint);
}
