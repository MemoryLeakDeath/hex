--liquibase formatted sql

--changeset memoryleakdeath:052720231900 dbms:postgresql
create table userdetails (
    userid uuid not null primary key,
    displayName varchar(75) not null,
    email varchar(100) not null,
    emailVerified boolean not null default false,
    lastUpdated timestamp not null,
    constraint fk_userdetails_identities foreign key(userid) references identities(id)
);
--rollback drop table if exists userdetails;
