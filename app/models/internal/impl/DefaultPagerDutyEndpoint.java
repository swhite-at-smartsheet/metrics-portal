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

import com.arpnetworking.commons.builder.OvalBuilder;
import com.arpnetworking.logback.annotations.Loggable;
import com.google.common.base.MoreObjects;
import models.internal.PagerDutyEndpoint;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

/**
 * Default internal model implementation for an alert.
 *
 * @author Ville Koskela (ville dot koskela at inscopemetrics dot com)
 */
@Loggable
public final class DefaultPagerDutyEndpoint implements PagerDutyEndpoint {

    @Override
    public UUID getId() {
        return _id;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public URI getAddress() {
        return _address;
    }

    @Override
    public String getServiceKey() {
        return _serviceKey;
    }


    @Override
    public models.view.PagerDutyEndpoint toView() {
        final models.view.PagerDutyEndpoint viewEndpoint = new models.view.PagerDutyEndpoint();
        viewEndpoint.setId(_id.toString());
        viewEndpoint.setName(_name);
        viewEndpoint.setAddress(_address);
        viewEndpoint.setServiceKey(_serviceKey);
        return viewEndpoint;
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
                .toString();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof DefaultPagerDutyEndpoint)) {
            return false;
        }

        final DefaultPagerDutyEndpoint otherAlert = (DefaultPagerDutyEndpoint) other;
        return Objects.equals(_id, otherAlert._id)
                && Objects.equals(_name, otherAlert._name)
                && Objects.equals(_address, otherAlert._address)
                && Objects.equals(_serviceKey, otherAlert._serviceKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                _id,
                _name,
                _address,
                _serviceKey);
    }

    private DefaultPagerDutyEndpoint(final Builder builder) {
        _id = builder._id;
        _name = builder._name;
        _address = builder._address;
        _serviceKey = builder._serviceKey;
    }

    private final UUID _id;
    private final String _name;
    private final URI _address;
    private final String _serviceKey;

    /**
     * Builder implementation for <code>PagerDutyEndpoint</code>.
     */
    public static final class Builder extends OvalBuilder<PagerDutyEndpoint> {

        /**
         * Public constructor.
         */
        public Builder() {
            super(DefaultPagerDutyEndpoint::new);
        }

        /**
         * The identifier. Required. Cannot be null.
         *
         * @param value The identifier.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setId(final UUID value) {
            _id = value;
            return this;
        }

        /**
         * The address. Required. Cannot be null or empty.
         *
         * @param value The address.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setAddress(final URI value) {
            _address = value;
            return this;
        }

        /**
         * The service key. Required. Cannot be null or empty.
         *
         * @param value The service key.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setServiceKey(final String value) {
            _serviceKey = value;
            return this;
        }

        /**
         * The name. Required. Cannot be null or empty.
         *
         * @param value The name.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setName(final String value) {
            _name = value;
            return this;
        }

        @NotNull
        private UUID _id;
        @NotNull
        @NotEmpty
        private String _name;
        @NotNull
        @NotEmpty
        private URI _address;
        @NotNull
        @NotEmpty
        private String _serviceKey;
    }
}
