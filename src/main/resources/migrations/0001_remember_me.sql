--liquibase formatted sql

--changeset memoryleakdeath:052020231700 dbms:postgresql
create table rememberme (
    userid uuid not null,
    token varchar(200) not null,
    lastUsed timestamp not null,
    expirationDate timestamp not null,
    primary key(userid, token),
    constraint fk_rememberme_identities foreign key(userid) references identities(id)
);
--rollback drop table if exists rememberme;
