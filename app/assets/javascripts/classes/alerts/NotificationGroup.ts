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

import ko = require('knockout');

export enum RecipientType {
    EMAIL = "email",
    WEBHOOK = "webhook",
    PAGERDUTY = "pagerduty"
}

export abstract class Recipient {
    type: RecipientType;

    constructor(type: RecipientType) {
        this.type = type;
    }
}

export class EmailRecipient extends Recipient {
    address: string;

    constructor(obj?: any) {
        super(RecipientType.EMAIL);
        if (obj !== undefined) {
            this.address = obj.address;
        }
    }
}

export class WebHookRecipient extends Recipient {
    address: string;

    constructor(obj?: any) {
        super(RecipientType.WEBHOOK);
        if (obj !== undefined) {
            this.address = obj.address;
        }
    }
}

export class PagerDutyRecipient extends Recipient {
    pagerDutyEndpointName: string;

    constructor(obj?: any) {
        super(RecipientType.PAGERDUTY);
        if (obj !== undefined) {
            this.pagerDutyEndpointName = obj.pagerDutyEndpointName;
        }
    }
}

export class NotificationGroup {
    id: string;
    name: string;
    entries: Recipient[] = [];
    editUri: KnockoutComputed<string>;

    constructor(id: string, name: string, recipients: any[]) {
        this.id = id;
        this.name = name;

        recipients.map(recipient => {
            if (recipient.type === "email") {
                return new EmailRecipient(recipient);
            } else if (recipient.type === "webhook") {
                return new WebHookRecipient(recipient);
            } else if (recipient.type === "pagerduty") {
                return new PagerDutyRecipient(recipient);
            }
        }).forEach(recipient => this.entries.push(recipient));

        this.editUri = ko.computed<string>(() => {
            return "#notificationgroup/edit/" + this.id;
        });
    }
}
