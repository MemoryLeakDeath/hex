--liquibase formatted sql

--changeset memoryleakdeath:01202024173300 dbms:postgresql
create type image_types as enum('gif', 'jpeg', 'png', 'svg');
--rollback drop type if exists image_types

--changeset memoryleakdeath:01202024173301 dbms:postgresql
create type approval_types as enum('channel approved', 'global approved', 'global rejected', 'rejected', 'pending');
--rollback drop type if exists approval_types

--changeset memoryleakdeath:01202024173302 dbms:postgresql
create table channelemotes (
    id uuid not null primary key,
    userid uuid not null,
    active boolean not null default true,
    allowed boolean not null default true,
    approvalStatus approval_types not null default 'pending',
    prefix varchar(5) not null,
    tag varchar(25) not null,
    name varchar(50) not null,
    subonly boolean not null,
    allowglobal boolean not null,
    smallimagefilename varchar(255) not null,
    smallimageurl varchar(255) not null,
    smallimagetype image_types not null,
    largeimagefilename varchar(255) null,
    largeimageurl varchar(255) null,
    largeimagetype image_types null,
    created timestamp not null,
    lastUpdated timestamp not null,
    constraint fk_channelemotes_identities foreign key(userid) references identities(id)
);
--rollback drop table if exists channelemotes;
