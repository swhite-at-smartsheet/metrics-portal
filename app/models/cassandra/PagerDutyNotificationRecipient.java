/**
 * Copyright 2018 Smartsheet.com
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

import models.internal.NotificationEntry;
import models.internal.impl.PagerDutyNotificationEntry;

import java.net.URI;
import java.util.Objects;

/**
 * Model class for an pagerduty notification recipient in cassandra.
 *
 * @author Brandon Arp (brandon dot arp at smartsheet dot com)
 */
public class PagerDutyNotificationRecipient implements NotificationRecipient {
    @Override
    public NotificationEntry toInternal() {
        return new PagerDutyNotificationEntry.Builder()
                .build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return (o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this);
    }
}