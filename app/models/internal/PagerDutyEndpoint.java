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
package models.internal;

import java.util.UUID;

/**
 * Internal model interface for an alert.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public interface PagerDutyEndpoint {

    /**
     * The unique identifier of the endpoint.
     *
     * @return The unique identifier of the endpoint.
     */
    UUID getId();

    /**
     * The name of the endpoint.
     *
     * @return The name of the endpoint.
     */
    String getName();

    /**
     * The address of the endpoint.
     *
     * @return The address.
     */
    String getPagerDutyUrl();

    /**
     * The service key of the endpoint.
     *
     * @return The service key.
     */
    String getServiceKey();

    /**
     * The comment.
     *
     * @return The comment.
     */
    String getComment();

    /**
     * Converts the model to a view model.
     *
     * @return a new view model
     */
    models.view.PagerDutyEndpoint toView();
}
