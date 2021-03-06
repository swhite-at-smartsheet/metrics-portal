/*
 * Copyright 2016 Groupon.com
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

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Data model for version sets. A <code>VersionSet</code> models a set of packages with corresponding versions.
 *
 * @author Matthew Hayter (mhayter at groupon dot com)
 */
// CHECKSTYLE.OFF: MemberNameCheck
@Entity
@Table(name = "version_sets", schema = "portal")
public class VersionSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    private UUID uuid;

    @CreatedTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdatedTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "version")
    private String version;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "version_set_package_versions",
            schema = "portal",
            joinColumns = @JoinColumn(name = "version_set_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "package_version_id", referencedColumnName = "id"))
    private List<PackageVersion> packageVersions;

    public Long getId() {
        return id;
    }

    public void setId(final Long value) {
        id = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(final UUID value) {
        uuid = value;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(final String value) {
        version = value;
    }

    public List<PackageVersion> getPackageVersions() {
        return packageVersions;
    }

    public void setPackageVersions(final List<PackageVersion> value) {
        packageVersions = value;
    }
}
// CHECKSTYLE.ON: MemberNameCheck
