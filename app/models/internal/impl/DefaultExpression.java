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
import models.internal.Expression;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Default internal model implementation for an expression.
 *
 * @author Ville Koskela (ville dot koskela at inscopemetrics dot com)
 */
@Loggable
public final class DefaultExpression implements Expression {

    @Override
    public UUID getId() {
        return _id;
    }

    @Override
    public String getCluster() {
        return _cluster;
    }

    @Override
    public String getService() {
        return _service;
    }

    @Override
    public String getMetric() {
        return _metric;
    }

    @Override
    public String getScript() {
        return _script;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", Integer.toHexString(System.identityHashCode(this)))
                .add("class", this.getClass())
                .add("Id", _id)
                .add("Cluster", _cluster)
                .add("Service", _service)
                .add("Metric", _metric)
                .add("Script", _script)
                .toString();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof DefaultExpression)) {
            return false;
        }

        final DefaultExpression otherExpression = (DefaultExpression) other;
        return Objects.equals(_id, otherExpression._id)
                && Objects.equals(_cluster, otherExpression._cluster)
                && Objects.equals(_service, otherExpression._service)
                && Objects.equals(_metric, otherExpression._metric)
                && Objects.equals(_script, otherExpression._script);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                _id,
                _cluster,
                _service,
                _metric,
                _script);
    }

    private DefaultExpression(final Builder builder) {
        _id = builder._id;
        _cluster = builder._cluster;
        _service = builder._service;
        _metric = builder._metric;
        _script = builder._script;
    }

    private final UUID _id;
    private final String _cluster;
    private final String _service;
    private final String _metric;
    private final String _script;

    /**
     * Builder implementation for <code>DefaultExpression</code>.
     */
    public static final class Builder extends OvalBuilder<DefaultExpression> {

        /**
         * Public constructor.
         */
        public Builder() {
            super(DefaultExpression::new);
        }

        /**
         * The identifier. Required. Cannot be null.
         *
         * @param value The identifier.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setId(@Nullable final UUID value) {
            _id = value;
            return this;
        }

        /**
         * The cluster. Required. Cannot be null or empty.
         *
         * @param value The cluster.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setCluster(final String value) {
            _cluster = value;
            return this;
        }

        /**
         * The service. Required. Cannot be null or empty.
         *
         * @param value The service.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setService(final String value) {
            _service = value;
            return this;
        }

        /**
         * The metric. Required. Cannot be null or empty.
         *
         * @param value The metric.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setMetric(final String value) {
            _metric = value;
            return this;
        }

        /**
         * The script. Required. Cannot be null or empty.
         *
         * @param value The script.
         * @return This instance of <code>Builder</code>.
         */
        public Builder setScript(final String value) {
            _script = value;
            return this;
        }

        @NotNull
        private UUID _id;
        @NotNull
        @NotEmpty
        private String _cluster;
        @NotNull
        @NotEmpty
        private String _service;
        @NotNull
        @NotEmpty
        private String _metric;
        @NotNull
        @NotEmpty
        private String _script;
    }
}
