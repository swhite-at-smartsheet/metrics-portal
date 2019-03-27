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
package models.ebean;


import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;
import models.internal.MetricsSoftwareState;
import models.internal.impl.DefaultHost;
import models.internal.impl.DefaultPagerDutyEndpoint;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Data model for pagerduty endpoints.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
// CHECKSTYLE.OFF: MemberNameCheck
@Entity
@Table(name = "pager_duty_endpoints", schema = "portal")
public class PagerDutyEndpoint {
    @Version
    @Column(name = "version")
    private Long version;

    @CreatedTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdatedTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "pagerduty_url")
    private String pagerDutyUrl;

    @Column(name = "service_key")
    private String serviceKey;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization")
    private Organization organization;

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long value) {
        version = value;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Timestamp value) {
        createdAt = value;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Timestamp value) {
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

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
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
}
// CHECKSTYLE.ON: MemberNameCheck
