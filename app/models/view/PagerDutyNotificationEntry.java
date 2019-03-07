/**
 * Copyright 2018 Smartsheet
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
package models.view;

/**
 * Represents an pagerduty notification entry.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public class PagerDutyNotificationEntry extends NotificationEntry {
    public String getAddress() {
        return _address;
    }

    public void setAddress(final String address) {
        _address = address;
    }

    private String _address;

    @Override
    public models.internal.NotificationEntry toInternal() {
        return new models.internal.impl.PagerDutyNotificationEntry.Builder()
                .setAddress(_address)
                .build();
    }
}
