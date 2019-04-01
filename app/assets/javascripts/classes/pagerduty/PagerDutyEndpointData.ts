/*
 * Copyright 2019 Smartsheet
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

class PagerDutyEndpointData {
    id: string;
    name: string;
    pagerDutyUrl: string;
    serviceKey: string;
    comment: string
    editUri: KnockoutComputed<string>;

    constructor(id: string, name: string, pagerDutyUrl: string, serviceKey: string, comment: string) {
        this.id = id;
        this.name = name;
        this.pagerDutyUrl = pagerDutyUrl;
        this.serviceKey = serviceKey;
        this.comment = comment;

        this.editUri = ko.computed<string>(() => {
            return "#pagerdutyendpoint/edit/" + this.id;
        });
    }
}

export = PagerDutyEndpointData;
