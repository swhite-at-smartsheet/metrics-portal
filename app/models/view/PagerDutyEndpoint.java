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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import models.internal.impl.DefaultEmailNotificationEntry;
import models.internal.impl.DefaultPagerDutyEndpoint;

import java.util.UUID;

/**
 * View model of <code>PagerDutyEndpoint</code>. Play view models are mutable.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
@Loggable
public final class PagerDutyEndpoint extends NotificationEntry {

    public void setId(final String value) {
        _id = value;
    }

    public String getId() {
        return _id;
    }

    public void setName(final String value) {
        _name = value;
    }

    public String getName() {
        return _name;
    }

    public void setAddress(final String value) {
        _address = value;
    }

    public String getAddress() {
        return _address;
    }

    public void setServiceKey(final String value) {
        _serviceKey = value;
    }

    public String getServiceKey() {
        return _serviceKey;
    }

    public String getComment() {
        return _comment;
    }

    public void setComment(final String comment) {
        _comment = comment;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", Integer.toHexString(System.identityHashCode(this)))
                .add("class", this.getClass())
                .add("Id", _id)
                .add("Name", _name)
                .add("Address", _address)
                .add("Service Key", _serviceKey)
                .add("Comment", _comment)
                .toString();
    }

    /**
     * Converts a view model to an internal model.
     *
     * @param objectMapper object mapper to convert some values
     * @return a new internal model
     */
    public models.internal.PagerDutyEndpoint toInternal(final ObjectMapper objectMapper) {
        final DefaultPagerDutyEndpoint.Builder endpointBuilder = new DefaultPagerDutyEndpoint.Builder()
                .setName(_name)
                .setAddress(_address)
                .setServiceKey(_serviceKey);
        if (_id != null) {
            endpointBuilder.setId(UUID.fromString(_id));
        }
        if (_comment != null) {
            endpointBuilder.setComment(_comment);
        }
        return endpointBuilder.build();
    }

    @Override
    public models.internal.NotificationEntry toInternal() {
        return new DefaultEmailNotificationEntry.Builder()
                .setAddress(_address)
                .build();
    }

    private String _id;
    private String _name;
    private String _address;
    private String _serviceKey;
    private String _comment;
}
