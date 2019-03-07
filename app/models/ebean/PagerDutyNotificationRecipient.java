/*
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
package models.ebean;

import com.google.common.base.Objects;
import models.internal.NotificationEntry;
import models.internal.impl.PagerDutyNotificationEntry;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Model class for a pagerduty notification recipient.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
// CHECKSTYLE.OFF: MemberNameCheck
@Entity
@DiscriminatorValue("pagerduty")
public class PagerDutyNotificationRecipient extends NotificationRecipient {
    @Column(name = "value")
    private String label = "pagerduty";

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        // only allow a single instance
        return o != null && getClass() == o.getClass();
    }


    @Override
    public int hashCode() {
        return Objects.hashCode("pagerduty");
    }

    @Override
    public NotificationEntry toInternal() {
        return new PagerDutyNotificationEntry.Builder()
                .build();
    }

    private static final long serialVersionUID = 1L;
}
// CHECKSTYLE.ON: MemberNameCheck
