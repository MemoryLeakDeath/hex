--liquibase formatted sql

--changeset memoryleakdeath:04222023143730 dbms:postgresql
create type tfa_types as enum('authapp');
--rollback drop type if exists tfa_types

--changeset memoryleakdeath:04222023143600 dbms:postgresql
create table identities (
    id uuid not null primary key default gen_random_uuid(),
    username varchar(50) not null,
    password varchar(100) not null,
    active boolean not null default false,
    failedAttempts smallint not null default 0,
    emailVerified boolean not null default false,
    useTfa boolean not null default false,
    tfaType tfa_types null default null,
    secret varchar(250) null default null,
    createdDate timestamp not null default now(),
    lastAttemptedLogin timestamp null default null
);
--rollback drop table if exists identities;

create unique index ix_identities_username on identities(username);
--rollback drop index if exists ix_identities_username;

--changeset memoryleakdeath:04222023143655 dbms:postgresql
create table authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    constraint fk_authorities_identities foreign key(username) references identities(username)
);
--rollback drop table if exists authorities;

create unique index ix_auth_username on authorities (username, authority);
--rollback drop index if exists ix_auth_username;


-- REMEMBER TO CLEAN PROJECT BEFORE RUNNING CHANGED MIGRATIONS!