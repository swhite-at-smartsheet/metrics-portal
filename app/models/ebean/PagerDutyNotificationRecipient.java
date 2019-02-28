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
package models.ebean;

import com.google.common.base.Objects;
import models.internal.NotificationEntry;
import models.internal.impl.PagerDutyNotificationEntry;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.net.URI;

/**
 * An email address to send mail to.
 *
 * @author Brandon Arp (brandon dot arp at smartsheet dot com)
 */
// CHECKSTYLE.OFF: MemberNameCheck
@Entity
@DiscriminatorValue("pagerduty")
public class PagerDutyNotificationRecipient extends NotificationRecipient {
    @Column(name = "value")
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return (o == null || getClass() != o.getClass());
    }

//    @Override
//    public int hashCode() {
//        return Objects.hashCode(this);
//    }

    @Override
    public NotificationEntry toInternal() {
        return new PagerDutyNotificationEntry.Builder()
                .build();
    }

    private static final long serialVersionUID = 1L;
}
// CHECKSTYLE.ON: MemberNameCheck