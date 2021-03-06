/*
 * Copyright 2018 Dropbox, Inc.
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

import PaginatedSearchableList = require("../PaginatedSearchableList");
import Report = require("./Report");

class ReportsList extends PaginatedSearchableList<Report> {
    fetchData(query: any, callback) {
        $.getJSON("v1/reports/query", query, (reportData) => {
            var hostList: Report[] = reportData.data.map((v: Report)=> { return null;});
            callback(hostList, reportData.pagination);
        })
    }

}

class ReportsViewModel {
    reports: ReportsList = new ReportsList();
    deletingId: string = null
    remove: (alert: Report) => void

    constructor() {
        this.reports.query();
    };
}
export = ReportsViewModel;
