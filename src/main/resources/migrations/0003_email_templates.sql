--liquibase formatted sql

--changeset memoryleakdeath:06162023225400 dbms:postgresql
create type email_template_types as enum('test','verification');
--rollback drop type if exists email_template_types

--changeset memoryleakdeath:06162023225500 dbms:postgresql
create table emailtemplates (
    id serial primary key,
    emailType email_template_types not null,
    locale varchar(100) null,
    subject varchar(4000) not null,
    body text not null
);
--rollback drop table if exists emailtemplates;
