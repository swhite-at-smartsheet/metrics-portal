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
package models.internal;

import java.util.List;
import java.util.UUID;

/**
 * Internal model interface for a VersionSet.
 *
 * @author Matthew Hayter (mhayter at groupon dot com)
 */
public interface VersionSet {

    /**
     * Accessor for the URI.
     *
     * @return the URI.
     */
    UUID getUuid();

    /**
     * Accessor for the version.
     *
     * @return the version.
     */
    String getVersion();

    /**
     * Accessor for the list of package versions associated with this version set.
     *
     * @return the list of package versions.
     */
    List<PackageVersion> getPackageVersions();
}
