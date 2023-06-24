--liquibase formatted sql

--changeset memoryleakdeath:06182023160600 dbms:postgresql
create table emailverification (
    userid uuid not null,
    token varchar(100) not null,
    expirationDate timestamp not null,
    primary key(userid, token),
    constraint fk_rememberme_identities foreign key(userid) references identities(id)
);
--rollback drop table if exists emailverification;
