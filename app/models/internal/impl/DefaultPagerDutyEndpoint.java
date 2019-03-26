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
import com.google.common.collect.ImmutableMap;
import models.internal.*;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import org.joda.time.Period;

import java.util.Objects;
import java.util.UUID;

/**
 * Default internal model implementation for an pagerduty endpoint.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
@Loggable
public final class DefaultPagerDutyEndpoint implements PagerDutyEndpoint {

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getPagerDutyUrl() {
        return _pagerDutyUrl;
    }

    @Override
    public String getServiceKey() {
        return _serviceKey;
    }

    @Override
    public String getComment() {
        return _comment;
    }

    @Override
    public models.view.PagerDutyEndpoint toView() {
        final models.view.PagerDutyEndpoint viewPagerDutyEndpoint = new models.view.PagerDutyEndpoint();
        viewPagerDutyEndpoint.setName(_name);
        viewPagerDutyEndpoint.setPagerDutyUrl(_pagerDutyUrl);
        viewPagerDutyEndpoint.setServiceKey(_serviceKey);
        viewPagerDutyEndpoint.setComment(_comment);
        return viewPagerDutyEndpoint;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", Integer.toHexString(System.identityHashCode(this)))
                .add("class", this.getClass())
                .add("Id", _id)
                .add("Name", _name)
                .add("PagerDuty URL", _pagerDutyUrl)
                .add("Service Key", _serviceKey)
                .add("Comment", _comment)
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
                && Objects.equals(_pagerDutyUrl, otherAlert._pagerDutyUrl)
                && Objects.equals(_serviceKey, otherAlert._serviceKey)
                && Objects.equals(_comment, otherAlert._comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                _id,
                _name,
                _pagerDutyUrl,
                _serviceKey,
                _comment);
    }

    private DefaultPagerDutyEndpoint(final Builder builder) {
        _id = builder._id;
        _name = builder._name;
        _pagerDutyUrl = builder._pagerDutyUrl;
        _serviceKey = builder._serviceKey;
        _comment = builder._comment;
    }

    private final UUID _id;
    private final String _name;
    private final String _pagerDutyUrl;
    private final String _serviceKey;
    private final String _comment;

    /**
     * Builder implementation for <code>DefaultAlert</code>.
     */
    public static final class Builder extends OvalBuilder<DefaultPagerDutyEndpoint> {

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
         * The name. Required. Cannot be null or empty.
         *
         * @param value The name.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setName(final String value) {
            _name = value;
            return this;
        }

        /**
         * The pagerduty url. Required. Cannot be null or empty.
         *
         * @param value The url.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setPagerDutyUrl(final String value) {
            _pagerDutyUrl = value;
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
         * Comment about the alert. Optional. Cannot be null. Defaults to empty.
         *
         * @param value Alert comment
         * @return This instance of <code>Builder</code>.
         */
        public Builder setComment(final String value) {
            _comment = value;
            return this;
        }

        @NotNull
        private UUID _id;
        @NotNull
        @NotEmpty
        private String _name;
        @NotNull
        @NotEmpty
        private String _pagerDutyUrl;
        @NotNull
        @NotEmpty
        private String _serviceKey;
        @NotNull
        private String _comment = "";
    }
}
