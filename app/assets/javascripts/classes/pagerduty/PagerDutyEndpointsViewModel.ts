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

import PagerDutyEndpointData = require('./PagerDutyEndpointData');
import PaginatedSearchableList = require('../PaginatedSearchableList');
import $ = require('jquery');
import csrf from '../Csrf'

class PagerDutyEndpointsList extends PaginatedSearchableList<PagerDutyEndpointData> {
    fetchData(query, callback) {
        $.getJSON("v1/pagerdutyendpoints/query", query, (data) => {
            const endpointsList: PagerDutyEndpointData[] = data.data.map((v: PagerDutyEndpointData)=> { return new PagerDutyEndpointData(
                v.id,
                v.name,
                v.pagerDutyUrl,
                v.serviceKey,
                v.comment
            );});
            callback(endpointsList, data.pagination);
        });
    }
}

class PagerDutyEndpointsViewModel {
    pagerDutyEndpoints: PagerDutyEndpointsList = new PagerDutyEndpointsList();
    deletingId: string = null;
    remove: (endpoint: PagerDutyEndpointData) => void;

    constructor() {
        this.pagerDutyEndpoints.query();
        this.remove = (endpoint: PagerDutyEndpointData) => {
            this.deletingId = endpoint.id;
            console.log("set deletingId: ", this, this.deletingId);

            $("#confirm-delete-modal").modal('show');
        };
    }

    confirmDelete() {
        $.ajax({
            type: "DELETE",
            url: "/v1/pagerdutyendpoints/" + this.deletingId,
            beforeSend: function(request) {
                request.setRequestHeader("Csrf-Token", csrf.getToken());
            },
            contentType: "application/json"
        }).done(() => {
            $("#confirm-delete-modal").modal('hide');
            this.pagerDutyEndpoints.query();
            this.deletingId = null;
        });
    }
}

export = PagerDutyEndpointsViewModel;
