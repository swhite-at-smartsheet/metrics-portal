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
package models.cassandra;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;
import models.internal.MetricsSoftwareState;
import models.internal.impl.DefaultHost;
import models.internal.impl.DefaultPagerDutyEndpoint;
import org.joda.time.Instant;

import javax.persistence.Version;
import java.util.UUID;

/**
 * Model for pagerduty endpoints stored in Cassandra.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
// CHECKSTYLE.OFF: MemberNameCheck
@Table(name = "pager_duty_endpoints", keyspace = "portal")
public class PagerDutyEndpoint {
    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "uuid")
    @PartitionKey
    private UUID uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "service_key")
    private String serviceKey;

    @Column(name = "comment")
    private String comment;

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long value) {
        version = value;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant value) {
        createdAt = value;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant value) {
        updatedAt = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String value) {
        name = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Converts this model into an {@link models.internal.Host}.
     *
     * @return a new internal model
     */
    public models.internal.PagerDutyEndpoint toInternal() {
        final DefaultPagerDutyEndpoint.Builder builder = new DefaultPagerDutyEndpoint.Builder()
                .setName(getName())
                .setAddress(getAddress())
                .setServiceKey(getServiceKey())
                .setComment(getComment());
        return builder.build();
    }
}
// CHECKSTYLE.ON: MemberNameCheck
