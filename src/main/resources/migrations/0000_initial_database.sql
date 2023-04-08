--liquibase formatted sql

--changeset memoryleakdeath:1 dbms:postgresql
create table users(
    id uuid not null primary key default gen_random_uuid(),
    username varchar(50) not null,
    password varchar(100) not null,
    active boolean not null default false
);
--rollback drop table if exists users;

create unique index ix_users_username on users(username);
--rollback drop index if exists ix_users_username;

--changeset memoryleakdeath:2 dbms:postgresql
create table authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    constraint fk_authorities_users foreign key(username) references users(username)
);
--rollback drop table if exists authorities;

create unique index ix_auth_username on authorities (username,authority);
--rollback drop index if exists ix_auth_username;


-- REMEMBER TO CLEAN PROJECT BEFORE RUNNING CHANGED MIGRATIONS!