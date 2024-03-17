--liquibase formatted sql

--changeset memoryleakdeath:03162024191845 dbms:postgresql
create table banaudits (
    id uuid not null primary key,
    targetuserid uuid not null,
    followeruserid uuid not null,
    moderatorid uuid not null,
    reason varchar(4000) null,
    action varchar(100) not null,
    created timestamp not null,
    lastUpdated timestamp not null,
    timedoutexpiration timestamp null,
    constraint fk_banaudits_target_identities foreign key(targetuserid) references identities(id),
    constraint fk_banaudits_follower_identities foreign key(followeruserid) references identities(id),
    constraint fk_banaudits_moderator_identities foreign key(moderatorid) references identities(id)
);
--rollback drop table if exists banaudits;
