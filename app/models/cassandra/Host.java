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
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.driver.mapping.annotations.Table;
import models.internal.MetricsSoftwareState;
import models.internal.impl.DefaultHost;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Version;

/**
 * Model for alerts stored in Cassandra.
 *
 * @author Brandon Arp (brandon dot arp at smartsheet dot com)
 */
// CHECKSTYLE.OFF: MemberNameCheck
@Table(name = "hosts", keyspace = "portal")
public class Host {
    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PartitionKey(1)
    @Column(name = "name")
    private String name;

    @Column(name = "cluster")
    private String cluster;

    @Column(name = "metrics_software_state")
    private String metricsSoftwareState;

    @PartitionKey(0)
    @Column(name = "organization")
    private UUID organization;

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

    public String getName() {
        return name;
    }

    public void setName(final String value) {
        name = value;
    }

    public UUID getOrganization() {
        return organization;
    }

    public void setOrganization(final UUID value) {
        organization = value;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(final String value) {
        cluster = value;
    }

    public String getMetricsSoftwareState() {
        return metricsSoftwareState;
    }

    public void setMetricsSoftwareState(final String value) {
        metricsSoftwareState = value;
    }

    /**
     * Converts this model into an {@link models.internal.Host}.
     *
     * @return a new internal model
     */
    public models.internal.Host toInternal() {
        final DefaultHost.Builder builder = new DefaultHost.Builder()
                .setHostname(getName())
                .setCluster(getCluster())
                .setMetricsSoftwareState(MetricsSoftwareState.valueOf(getMetricsSoftwareState()));
        return builder.build();
    }

    /**
     * Queries for hosts.
     *
     * @author Brandon Arp (brandon dot arp at smartsheet dot com)
     */
    @Accessor
    public interface HostQueries {
        /**
         * Queries for all hosts in an organization.
         *
         * @param organization Organization owning the alerts
         * @return Mapped query results
         */
        @Query("select * from portal.hosts_by_organization where organization = :org")
        Result<Host> getHostsForOrganization(@Param("org") UUID organization);
    }
}
// CHECKSTYLE.ON: MemberNameCheck
