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
package models.view;

import com.arpnetworking.logback.annotations.Loggable;
import com.arpnetworking.metrics.portal.notifications.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import models.internal.Organization;
import models.internal.impl.DefaultPagerDutyEndpoint;

import java.net.URI;
import java.util.UUID;

/**
 * View model of <code>DefaultPagerDutyEndpoint</code>. Play view models are mutable.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
@Loggable
public final class PagerDutyEndpoint {
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public URI getAddress() {
        return _address;
    }

    public void setAddress(URI _address) {
        this._address = _address;
    }

    public String getServiceKey() {
        return _serviceKey;
    }

    public void setServiceKey(String _serviceKey) {
        this._serviceKey = _serviceKey;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", Integer.toHexString(System.identityHashCode(this)))
                .add("class", this.getClass())
                .add("Name", _name)
                .add("Address", _address)
                .add("Service Key", _serviceKey)
                .toString();
    }

    /**
     * Converts a view model to an internal model.
     *
     * @param organization organization the DefaultPagerDutyEndpoint belongs to
     * @param notificationRepository notification repository to resolve notification groups
     * @param objectMapper object mapper to convert some values
     * @return a new internal model
     */
    public models.internal.PagerDutyEndpoint toInternal(
            final Organization organization,
        final NotificationRepository notificationRepository,
        final ObjectMapper objectMapper) {
        final DefaultPagerDutyEndpoint.Builder builder = new DefaultPagerDutyEndpoint.Builder()
                .setName(_name)
                .setAddress(_address)
                .setServiceKey(_serviceKey);
        if (_id != null) {
            builder.setId(UUID.fromString(_id));
        }

        return builder.build();
    }

    private String _id;
    private String _name;
    private URI _address;
    private String _serviceKey;
}
