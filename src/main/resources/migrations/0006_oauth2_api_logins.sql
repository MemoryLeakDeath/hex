--liquibase formatted sql

--changeset memoryleakdeath:073120232307 dbms:postgresql
create table if not exists oauth2clientregistry (
    id varchar(100) NOT NULL PRIMARY KEY,
    client_id varchar(100) NOT NULL,
    client_id_issued_at timestamp DEFAULT NOW() NOT NULL,
    client_secret varchar(200) DEFAULT NULL,
    client_secret_expires_at timestamp DEFAULT NULL,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris varchar(1000) DEFAULT NULL,
    scopes varchar(1000) NOT NULL
)
--rollback drop table if exists oauth2clientregistry

--changeset memoryleakdeath:080520231600 dbms:postgresql
CREATE TABLE IF NOT EXISTS oauth2authorization (
    id varchar(100) NOT NULL PRIMARY KEY,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000) DEFAULT NULL,
    attributes text DEFAULT NULL,
    state varchar(500) DEFAULT NULL,
    authorization_code_value text DEFAULT NULL,
    authorization_code_issued_at timestamp DEFAULT NULL,
    authorization_code_expires_at timestamp DEFAULT NULL,
    authorization_code_metadata text DEFAULT NULL,
    access_token_value text DEFAULT NULL,
    access_token_issued_at timestamp DEFAULT NULL,
    access_token_expires_at timestamp DEFAULT NULL,
    access_token_metadata text DEFAULT NULL,
    access_token_type varchar(100) DEFAULT NULL,
    access_token_scopes varchar(1000) DEFAULT NULL,
    refresh_token_value text DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT NULL,
    refresh_token_expires_at timestamp DEFAULT NULL,
    refresh_token_metadata text DEFAULT NULL,
    user_code_value text DEFAULT NULL,
    user_code_issued_at timestamp DEFAULT NULL,
    user_code_expires_at timestamp DEFAULT NULL,
    user_code_metadata text DEFAULT NULL
);
--rollback drop table if exists oauth2authorization

--changeset memoryleakdeath:080520231605 dbms:postgresql
CREATE TABLE oauth2authorizationconsent (
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);
--rollback drop table if exists oauth2authorizationconsent

--changeset memoryleakdeath:082920232420 dbms:postgresql
CREATE TABLE clientownerregistry (
	userid UUID NOT NULL references identities(id),
	registeredclientid varchar(100) NOT NULL,
	PRIMARY KEY (userid, registeredclientid)
)
--rollback drop table if exists clientownerregistry
