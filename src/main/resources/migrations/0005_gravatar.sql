--liquibase formatted sql

--changeset memoryleakdeath:0701183800 dbms:postgresql
alter table if exists userdetails
    add column if not exists gravatarId varchar(32) not null default '';

--rollback alter table if exists userdetails
--rollback    drop column if exists gravatarId;
