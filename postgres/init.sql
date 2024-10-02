CREATE DATABASE customer;
CREATE DATABASE product;
CREATE DATABASE orders;
CREATE DATABASE delivery;
CREATE DATABASE commerce_test;

CREATE TABLE keycloak_access (
    id BIGSERIAL PRIMARY KEY,
    realm VARCHAR(255),
    client_id VARCHAR(255),
    client_secret VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255)
);

INSERT INTO keycloak_access (realm, client_id, client_secret, username, password)
VALUES ('commerce', 'gateway', 'ouQ51dghJBhO9UIS3WGC1Mz7GRpMK5Tj', 'auth', '123');

