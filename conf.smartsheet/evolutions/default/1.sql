# --- !Ups

create table portal.alerts (
  id                        bigint auto_increment not null,
  uuid                      varchar(40),
  name                      varchar(255),
  cluster                   varchar(255),
  service                   varchar(255),
  context                   varchar(7),
  metric                    varchar(255),
  statistic                 varchar(255),
  period_in_seconds         integer,
  operator                  varchar(24),
  quantity_value            double,
  quantity_unit             varchar(255),
  organization              bigint not null,
  version                   bigint not null,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint ck_alerts_context check (context in ('HOST','CLUSTER')),
  constraint ck_alerts_operator check (operator in ('EQUAL_TO','NOT_EQUAL_TO','LESS_THAN','LESS_THAN_OR_EQUAL_TO','GREATER_THAN','GREATER_THAN_OR_EQUAL_TO')),
  constraint pk_alerts primary key (id))
;

create table portal.expressions (
  id                        bigint auto_increment not null,
  uuid                      varchar(40),
  cluster                   varchar(255),
  service                   varchar(255),
  metric                    varchar(255),
  script                    varchar(255),
  organization              bigint not null,
  version                   bigint not null,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_expressions primary key (id))
;

create table portal.hosts (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  cluster                   varchar(255),
  metrics_software_state    varchar(255),
  organization              bigint not null,
  version                   bigint not null,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_hosts primary key (id))
;

create table portal.nagios_extensions (
  id                        bigint auto_increment not null,
  alert_id                  bigint,
  severity                  varchar(255),
  notify                    varchar(255),
  max_check_attempts        integer,
  freshness_threshold_in_seconds bigint,
  version                   bigint not null,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_nagios_extensions_alert_id unique (alert_id),
  constraint pk_nagios_extensions primary key (id))
;

create table portal.organizations (
  id                        bigint auto_increment not null,
  uuid                      varchar(40),
  name                      varchar(255),
  version                   bigint not null,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_organizations primary key (id))
;

create table portal.package_versions (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  version                   varchar(255),
  uri                       varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_package_versions primary key (id))
;

create table portal.version_sets (
  id                        bigint auto_increment not null,
  uuid                      varchar(40),
  version                   varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_version_sets primary key (id))
;

create table portal.version_specifications (
  id                        bigint auto_increment not null,
  uuid                      varchar(40),
  next                      bigint,
  version_set_id            bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_version_specifications_next unique (next),
  constraint pk_version_specifications primary key (id))
;

create table portal.version_specification_attributes (
  id                        bigint auto_increment not null,
  keyName                   varchar(255),
  value                     varchar(255),
  version_specification     bigint,
  constraint pk_version_specification_attribu primary key (id))
;


create table portal.version_set_package_versions (
  version_set_id                 bigint not null,
  package_version_id             bigint not null,
  constraint pk_portal.version_set_package_versions primary key (version_set_id, package_version_id))
;
alter table portal.alerts add constraint fk_alerts_organization_1 foreign key (organization) references portal.organizations (id) on delete restrict on update restrict;
create index ix_alerts_organization_1 on portal.alerts (organization);
alter table portal.expressions add constraint fk_expressions_organization_2 foreign key (organization) references portal.organizations (id) on delete restrict on update restrict;
create index ix_expressions_organization_2 on portal.expressions (organization);
alter table portal.hosts add constraint fk_hosts_organization_3 foreign key (organization) references portal.organizations (id) on delete restrict on update restrict;
create index ix_hosts_organization_3 on portal.hosts (organization);
alter table portal.nagios_extensions add constraint fk_nagios_extensions_alert_4 foreign key (alert_id) references portal.alerts (id) on delete restrict on update restrict;
create index ix_nagios_extensions_alert_4 on portal.nagios_extensions (alert_id);
alter table portal.version_specifications add constraint fk_version_specifications_next_5 foreign key (next) references portal.version_specifications (id) on delete restrict on update restrict;
create index ix_version_specifications_next_5 on portal.version_specifications (next);
alter table portal.version_specifications add constraint fk_version_specifications_vers_6 foreign key (version_set_id) references portal.version_sets (id) on delete restrict on update restrict;
create index ix_version_specifications_vers_6 on portal.version_specifications (version_set_id);
alter table portal.version_specification_attributes add constraint fk_version_specification_attri_7 foreign key (version_specification) references portal.version_specifications (id) on delete restrict on update restrict;
create index ix_version_specification_attri_7 on portal.version_specification_attributes (version_specification);



alter table portal.version_set_package_versions add constraint fk_portal.version_set_package_01 foreign key (version_set_id) references portal.version_sets (id) on delete restrict on update restrict;

alter table portal.version_set_package_versions add constraint fk_portal.version_set_package_02 foreign key (package_version_id) references portal.package_versions (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists portal.alerts;

drop table if exists portal.expressions;

drop table if exists portal.hosts;

drop table if exists portal.nagios_extensions;

drop table if exists portal.organizations;

drop table if exists portal.package_versions;

drop table if exists portal.version_sets;

drop table if exists portal.version_set_package_versions;

drop table if exists portal.version_specifications;

drop table if exists portal.version_specification_attributes;

SET REFERENTIAL_INTEGRITY TRUE;

