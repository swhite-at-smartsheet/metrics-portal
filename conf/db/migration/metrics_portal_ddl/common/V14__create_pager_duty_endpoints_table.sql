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
 CREATE TABLE PORTAL.PAGER_DUTY_ENDPOINTS (
  id                                 BIGINT AUTO_INCREMENT NOT NULL,
  uuid                               UUID,
  name                               VARCHAR(255),
  pager_duty_url                     VARCHAR(255),
  service_key                        VARCHAR(255),
  comment                            VARCHAR(255),
  organization                       BIGINT NOT NULL,
  version                            BIGINT NOT NULL,
  created_at                         TIMESTAMP NOT NULL,
  updated_at                         TIMESTAMP NOT NULL,
  CONSTRAINT pk_pager_duty_endpoints PRIMARY KEY (id)
);

ALTER TABLE portal.pager_duty_endpoints ADD CONSTRAINT fk_pager_duty_endpoints_organization FOREIGN KEY (organization) REFERENCES portal.organizations (id) ON DELETE RESTRICT ON UPDATE RESTRICT;
CREATE INDEX ix_pager_duty_endpoints_organization ON portal.pager_duty_endpoints (organization);
CREATE UNIQUE INDEX ix_pager_duty_endpoints_name_organization ON portal.pager_duty_endpoints (name, organization);
