/*
 * Copyright 2019 Smartsheet.com
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

import java.util.Objects;

/**
 * Model class for a pagerduty notification recipient in cassandra.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public class PagerDutyNotificationRecipient implements NotificationRecipient {
    public String getPagerDutyEndpointName() {
        return _pagerDutyEndpointName;
    }

    public void setPagerDutyEndpointName(final String pagerDutyEndpointName) {
        _pagerDutyEndpointName = pagerDutyEndpointName;
    }

    @Override
    public NotificationEntry toInternal() {
        return new PagerDutyNotificationEntry.Builder()
                .setPagerDutyEndpointName(_pagerDutyEndpointName)
                .build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PagerDutyNotificationRecipient that = (PagerDutyNotificationRecipient) o;
        return Objects.equals(_pagerDutyEndpointName, that._pagerDutyEndpointName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_pagerDutyEndpointName);
    }

    private String _pagerDutyEndpointName;
}
