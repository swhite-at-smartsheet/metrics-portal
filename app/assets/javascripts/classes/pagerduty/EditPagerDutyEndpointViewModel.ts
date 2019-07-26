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

import ko = require('knockout');
import $ = require('jquery');
import uuid = require('../Uuid');
import PagerDutyEndpointData = require("./PagerDutyEndpointData");
import csrf from '../Csrf';


class EditPagerDutyEndpointViewModel {
    id = ko.observable<string>("");
    name = ko.observable<string>("");
    pagerDutyUrl = ko.observable<string>("");
    serviceKey = ko.observable<string>("");
    comment = ko.observable<string>("");
    container: HTMLElement;

    activate(id: string) {
        if (id != null) {
            this.loadEndpoint(id);
        } else {
            this.id(uuid.v4());
        }
    }

    loadEndpoint(id: string): void {
        $.getJSON("/v1/pagerdutyendpoints/" + id, {}, (data: PagerDutyEndpointData) => {
            this.id(data.id);
            this.name(data.name);
            this.pagerDutyUrl(data.pagerDutyUrl);
            this.serviceKey(data.serviceKey);
            this.comment(data.comment);
        });
    }

    save(): void {
        $.ajax({
            type: "PUT",
            url: "/v1/pagerdutyendpoints",
            beforeSend: function(request) {
                request.setRequestHeader("Csrf-Token", csrf.getToken());
            },
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify({
                "id": this.id(),
                "name": this.name(),
                "pagerDutyUrl": this.pagerDutyUrl(),
                "serviceKey": this.serviceKey(),
                "comment": this.comment()
            }),
        }).done(() => {
            window.location.href = "/#pagerdutyendpoints";
        });
    }

    cancel(): void {
        window.location.href = "/#pagerdutyendpoints";
    }
}

export = EditPagerDutyEndpointViewModel;
