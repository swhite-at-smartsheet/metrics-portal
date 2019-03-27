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

    @PartitionKey
    @Column(name = "name")
    private String name;

    @Column(name = "pagerduty_url")
    private String pagerDutyUrl;

    @Column(name = "service_key")
    private String serviceKey;

    @Column(name = "comment")
    private String comment;

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

    public String getPagerDutyUrl() {
        return pagerDutyUrl;
    }

    public void setPagerDutyUrl(String pagerDutyUrl) {
        this.pagerDutyUrl = pagerDutyUrl;
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

    public UUID getOrganization() {
        return organization;
    }

    public void setOrganization(UUID organization) {
        this.organization = organization;
    }

    /**
     * Converts this model into an {@link models.internal.Host}.
     *
     * @return a new internal model
     */
    public models.internal.PagerDutyEndpoint toInternal() {
        final DefaultPagerDutyEndpoint.Builder builder = new DefaultPagerDutyEndpoint.Builder()
                .setName(getName())
                .setPagerDutyUrl(getPagerDutyUrl())
                .setServiceKey(getServiceKey())
                .setComment(getComment());
        return builder.build();
    }

    /**
     * Queries for pagerduty endpoints.
     *
     * @author Sheldon White (sheldon.white at smartsheet dot com)
     */
    @Accessor
    public interface PagerDutyEndpointQueries {
        /**
         * Queries for all pagerduty endpoints in an organization.
         *
         * @param organization Organization owning the endpoints
         * @return Mapped query results
         */
        @Query("select * from portal.pagerduty_endpoints_by_organization where organization = :org")
        Result<PagerDutyEndpoint> getEndpointsForOrganization(@Param("org") UUID organization);
    }
}
// CHECKSTYLE.ON: MemberNameCheck
