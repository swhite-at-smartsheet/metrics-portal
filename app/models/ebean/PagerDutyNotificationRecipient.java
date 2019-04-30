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
package models.ebean;

import models.internal.NotificationEntry;
import models.internal.impl.PagerDutyNotificationEntry;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Objects;

/**
 * Model class for a pagerduty notification recipient.
 * These do not have any internal state and act as singletons when creating a NotificationGroup.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
// CHECKSTYLE.OFF: MemberNameCheck
@Entity
@DiscriminatorValue("pagerduty")
public class PagerDutyNotificationRecipient extends NotificationRecipient {
    @Column(name = "value")
    private String _endpointName;

    public String getAddress() {
        return _endpointName;
    }

    public void setEndpointName(final String endpointName) {
        _endpointName = endpointName;
    }

    @Override
    public NotificationEntry toInternal() {
        return new PagerDutyNotificationEntry.Builder()
                .setEndpointName(_endpointName)
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
        return Objects.equals(_endpointName, that._endpointName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(_endpointName);
    }

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
}
// CHECKSTYLE.ON: MemberNameCheck
