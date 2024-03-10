--liquibase formatted sql

--changeset memoryleakdeath:030924164445 dbms:postgresql
create table followers (
    targetuserid uuid not null,
    followeruserid uuid not null,
    active boolean not null default true,
    banned boolean not null default false,
    timedout boolean not null default false,
    notifications boolean not null default false,
    created timestamp not null,
    lastUpdated timestamp not null,
    timedoutexpiration timestamp null,
    constraint fk_followers_target_identities foreign key(targetuserid) references identities(id),
    constraint fk_followers_follower_identities foreign key(followeruserid) references identities(id),
    primary key(targetuserid, followeruserid)
);
--rollback drop table if exists followers;
