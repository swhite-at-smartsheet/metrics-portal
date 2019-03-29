# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table if not exists portal.alerts (
  id                            bigint auto_increment not null,
  uuid                          uuid,
  name                          varchar(255),
  query                         clob,
  period_in_seconds             integer not null,
  comment                       varchar(255),
  notification_group            bigint,
  organization                  bigint not null,
  version                       bigint not null,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_alerts primary key (id)
);

create table portal.alerts_etags (
  id                            bigint auto_increment not null,
  organization                  bigint not null,
  etag                          bigint not null,
  constraint pk_alerts_etags primary key (id)
);

create table portal.expressions (
  id                            bigint auto_increment not null,
  uuid                          uuid,
  cluster                       varchar(255),
  service                       varchar(255),
  metric                        varchar(255),
  script                        varchar(255),
  organization                  bigint not null,
  version                       bigint not null,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_expressions primary key (id)
);

create table portal.expressions_etags (
  id                            bigint auto_increment not null,
  organization                  bigint not null,
  etag                          bigint not null,
  constraint pk_expressions_etags primary key (id)
);

create table portal.hosts (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  cluster                       varchar(255),
  metrics_software_state        varchar(255),
  organization                  bigint not null,
  version                       bigint not null,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_hosts primary key (id)
);

create table portal.nagios_extensions (
  id                            bigint auto_increment not null,
  alert_id                      bigint,
  severity                      varchar(255),
  notify                        varchar(255),
  max_check_attempts            integer not null,
  freshness_threshold_in_seconds bigint not null,
  version                       bigint not null,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint uq_nagios_extensions_alert_id unique (alert_id),
  constraint pk_nagios_extensions primary key (id)
);

create table portal.notification_groups (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  uuid                          uuid,
  organization                  bigint not null,
  constraint pk_notification_groups primary key (id)
);

create table portal.notification_recipients (
  type                          varchar(31) not null,
  id                            bigint auto_increment not null,
  notificationgroup             bigint,
  value                         varchar(255),
  constraint pk_notification_recipients primary key (id)
);

create table portal.organizations (
  id                            bigint auto_increment not null,
  uuid                          uuid,
  version                       bigint not null,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_organizations primary key (id)
);

create table portal.package_versions (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  version                       varchar(255),
  uri                           varchar(255),
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_package_versions primary key (id)
);

create table portal.version_sets (
  id                            bigint auto_increment not null,
  uuid                          uuid,
  version                       varchar(255),
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_version_sets primary key (id)
);

create table portal.version_set_package_versions (
  version_set_id                bigint not null,
  package_version_id            bigint not null,
  constraint pk_version_set_package_versions primary key (version_set_id,package_version_id)
);

create table portal.version_specifications (
  id                            bigint auto_increment not null,
  uuid                          uuid,
  next                          bigint,
  version_set_id                bigint,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint uq_version_specifications_next unique (next),
  constraint pk_version_specifications primary key (id)
);

create table portal.version_specification_attributes (
  id                            bigint auto_increment not null,
  keyname                       varchar(255),
  value                         varchar(255),
  version_specification         bigint,
  constraint pk_version_specification_attributes primary key (id)
);

alter table portal.alerts add constraint fk_alerts_notification_group foreign key (notification_group) references portal.notification_groups (id) on delete restrict on update restrict;
create index ix_alerts_notification_group on portal.alerts (notification_group);

alter table portal.alerts add constraint fk_alerts_organization foreign key (organization) references portal.organizations (id) on delete restrict on update restrict;
create index ix_alerts_organization on portal.alerts (organization);

alter table portal.alerts_etags add constraint fk_alerts_etags_organization foreign key (organization) references portal.organizations (id) on delete restrict on update restrict;
create index ix_alerts_etags_organization on portal.alerts_etags (organization);

alter table portal.expressions add constraint fk_expressions_organization foreign key (organization) references portal.organizations (id) on delete restrict on update restrict;
create index ix_expressions_organization on portal.expressions (organization);

alter table portal.expressions_etags add constraint fk_expressions_etags_organization foreign key (organization) references portal.organizations (id) on delete restrict on update restrict;
create index ix_expressions_etags_organization on portal.expressions_etags (organization);

alter table portal.hosts add constraint fk_hosts_organization foreign key (organization) references portal.organizations (id) on delete restrict on update restrict;
create index ix_hosts_organization on portal.hosts (organization);

alter table portal.nagios_extensions add constraint fk_nagios_extensions_alert_id foreign key (alert_id) references portal.alerts (id) on delete restrict on update restrict;

alter table portal.notification_groups add constraint fk_notification_groups_organization foreign key (organization) references portal.organizations (id) on delete restrict on update restrict;
create index ix_notification_groups_organization on portal.notification_groups (organization);

alter table portal.notification_recipients add constraint fk_notification_recipients_notificationgroup foreign key (notificationgroup) references portal.notification_groups (id) on delete restrict on update restrict;
create index ix_notification_recipients_notificationgroup on portal.notification_recipients (notificationgroup);

alter table portal.version_set_package_versions add constraint fk_version_set_package_versions_version_sets foreign key (version_set_id) references portal.version_sets (id) on delete restrict on update restrict;
create index ix_version_set_package_versions_version_sets on portal.version_set_package_versions (version_set_id);

alter table portal.version_set_package_versions add constraint fk_version_set_package_versions_package_versions foreign key (package_version_id) references portal.package_versions (id) on delete restrict on update restrict;
create index ix_version_set_package_versions_package_versions on portal.version_set_package_versions (package_version_id);

alter table portal.version_specifications add constraint fk_version_specifications_next foreign key (next) references portal.version_specifications (id) on delete restrict on update restrict;

alter table portal.version_specifications add constraint fk_version_specifications_version_set_id foreign key (version_set_id) references portal.version_sets (id) on delete restrict on update restrict;
create index ix_version_specifications_version_set_id on portal.version_specifications (version_set_id);

alter table portal.version_specification_attributes add constraint fk_version_specification_attributes_version_specification foreign key (version_specification) references portal.version_specifications (id) on delete restrict on update restrict;
create index ix_version_specification_attributes_version_specification on portal.version_specification_attributes (version_specification);


# --- !Downs

-- alter table portal.alerts drop constraint if exists fk_alerts_notification_group;
-- drop index if exists ix_alerts_notification_group;
--
-- alter table portal.alerts drop constraint if exists fk_alerts_organization;
-- drop index if exists ix_alerts_organization;
--
-- alter table portal.alerts_etags drop constraint if exists fk_alerts_etags_organization;
-- drop index if exists ix_alerts_etags_organization;
--
-- alter table portal.expressions drop constraint if exists fk_expressions_organization;
-- drop index if exists ix_expressions_organization;
--
-- alter table portal.expressions_etags drop constraint if exists fk_expressions_etags_organization;
-- drop index if exists ix_expressions_etags_organization;
--
-- alter table portal.hosts drop constraint if exists fk_hosts_organization;
-- drop index if exists ix_hosts_organization;
--
-- alter table portal.nagios_extensions drop constraint if exists fk_nagios_extensions_alert_id;
--
-- alter table portal.notification_groups drop constraint if exists fk_notification_groups_organization;
-- drop index if exists ix_notification_groups_organization;
--
-- alter table portal.notification_recipients drop constraint if exists fk_notification_recipients_notificationgroup;
-- drop index if exists ix_notification_recipients_notificationgroup;
--
-- alter table portal.version_set_package_versions drop constraint if exists fk_version_set_package_versions_version_sets;
-- drop index if exists ix_version_set_package_versions_version_sets;
--
-- alter table portal.version_set_package_versions drop constraint if exists fk_version_set_package_versions_package_versions;
-- drop index if exists ix_version_set_package_versions_package_versions;
--
-- alter table portal.version_specifications drop constraint if exists fk_version_specifications_next;
--
-- alter table portal.version_specifications drop constraint if exists fk_version_specifications_version_set_id;
-- drop index if exists ix_version_specifications_version_set_id;
--
-- alter table portal.version_specification_attributes drop constraint if exists fk_version_specification_attributes_version_specification;
-- drop index if exists ix_version_specification_attributes_version_specification;
--
-- drop table if exists portal.alerts;
--
-- drop table if exists portal.alerts_etags;
--
-- drop table if exists portal.expressions;
--
-- drop table if exists portal.expressions_etags;
--
-- drop table if exists portal.hosts;
--
-- drop table if exists portal.nagios_extensions;
--
-- drop table if exists portal.notification_groups;
--
-- drop table if exists portal.notification_recipients;
--
-- drop table if exists portal.organizations;
--
-- drop table if exists portal.package_versions;
--
-- drop table if exists portal.version_sets;
--
-- drop table if exists portal.version_set_package_versions;
--
-- drop table if exists portal.version_specifications;
--
-- drop table if exists portal.version_specification_attributes;

