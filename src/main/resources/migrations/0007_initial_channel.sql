--liquibase formatted sql

--changeset memoryleakdeath:11102023230955 dbms:postgresql
create table channels (
    userid uuid not null primary key,
    name varchar(75) not null,
    active boolean not null default true,
    title varchar(500) null,
    live boolean not null default false,
    settings jsonb null,
    created timestamp not null,
    lastUpdated timestamp not null,
    constraint fk_channels_identities foreign key(userid) references identities(id)
);
--rollback drop table if exists channels;
