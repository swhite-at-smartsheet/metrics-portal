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
package models.internal;

import java.net.URI;
import java.util.UUID;

/**
 * Internal model interface for an pagerduty API endpoint.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public interface PagerDutyEndpoint {

    /**
     * The unique identifier of the alert.
     *
     * @return The unique identifier of the alert.
     */
    UUID getId();

    /**
     * The human-readable string for the endpoint.
     *
     * @return The organization that owns the alert.
     */
    String getName();

    /**
     * The address of the endpoint.
     *
     * @return The address.
     */
    URI getAddress();

    /**
     * The service key for the endpoint.
     *
     * @return The service key.
     */
    String getServiceKey();

    /**
     * Converts the model to a view model.
     *
     * @return a new view model
     */
    models.view.PagerDutyEndpoint toView();
}
