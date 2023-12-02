--liquibase formatted sql

--changeset memoryleakdeath:12022023002200 dbms:postgresql
create table chatmessages (
    messageid uuid not null primary key,
    senderid uuid not null,
    channelid uuid not null,
    created timestamp not null,
    lastUpdated timestamp not null,
    visible boolean not null default true,
    message varchar(2000) not null,
    details jsonb null,
    constraint fk_chatmessages_identities foreign key(senderid) references identities(id),
    constraint fk_chatmessages_channels foreign key(channelid) references channels(userid)
);
--rollback drop table if exists chatmessages;
