DROP TABLE IF EXISTS sensor_data_aud;
DROP TABLE IF EXISTS sensor_station_aud;
DROP TABLE IF EXISTS access_point_aud;
DROP TABLE IF EXISTS userx_user_role_aud;
DROP TABLE IF EXISTS userx_aud;
DROP TABLE IF EXISTS revinfo;
DROP TABLE IF EXISTS log_info_seq;
DROP TABLE IF EXISTS users_favourites_aud;
DROP TABLE IF EXISTS log_info;
DROP TABLE IF EXISTS sensor_data;
DROP TABLE IF EXISTS sensor_station_gardener;
DROP TABLE IF EXISTS users_favourites;
DROP TABLE IF EXISTS sensor_station;
DROP TABLE IF EXISTS access_point;
DROP TABLE IF EXISTS userx_user_role;
DROP TABLE IF EXISTS userx;

CREATE TABLE access_point
(
    id                VARCHAR(255) NOT NULL,
    create_date       timestamp    NULL,
    connected         BOOLEAN      NULL,
    enabled           BOOLEAN      NULL,
    sending_interval  DOUBLE       NULL,
    update_date       timestamp    NULL,
    access_point_name VARCHAR(255) NULL,
    password          VARCHAR(255),
    access_Point_Role VARCHAR(100),
    CONSTRAINT pk_accesspoint PRIMARY KEY (id)
);

CREATE TABLE sensor_data
(
    id                VARCHAR(255) NOT NULL,
    measurement       DOUBLE       NOT NULL,
    sensor_station_id VARCHAR(255) NULL,
    type              VARCHAR(255) NULL,
    unit              VARCHAR(255) NULL,
    timestamp         timestamp    NULL,
    create_date       timestamp    NOT NULL,
    CONSTRAINT pk_sensordata PRIMARY KEY (id)
);

CREATE TABLE sensor_station
(
    id                    VARCHAR(255) NOT NULL,
    connected             BOOLEAN      NULL,
    enabled               BOOLEAN      NULL,
    dip_id                BIGINT       NULL,
    name                  VARCHAR(255) NULL,
    category              VARCHAR(255) NULL,
    transmission_interval DOUBLE       NULL,
    create_date           timestamp    NOT NULL,
    access_point_id       VARCHAR(255) NULL,
    gardener_id           VARCHAR(255) NULL ,
    CONSTRAINT pk_sensorstation PRIMARY KEY (id)
);


CREATE TABLE userx
(
    username             VARCHAR(100) NOT NULL,
    create_user_username VARCHAR(100) NULL,
    create_date          timestamp    NOT NULL,
    update_user_username VARCHAR(100) NULL,
    update_date          timestamp    NULL,
    password             VARCHAR(255) NULL,
    first_name           VARCHAR(255) NULL,
    last_name            VARCHAR(255) NULL,
    email                VARCHAR(255) NULL,
    phone                VARCHAR(255) NULL,
    enabled              BOOLEAN      NOT NULL,
    CONSTRAINT pk_userx PRIMARY KEY (username)
);

CREATE TABLE userx_user_role
(
    userx_username VARCHAR(100) NOT NULL,
    roles          VARCHAR(255) NULL
);

CREATE TABLE users_favourites
(
    id             VARCHAR(255) NOT NULL,
    username       VARCHAR(255) NOT NULL,
    sensor_station_id VARCHAR(255) NOT NULL,
    create_date          timestamp    NOT NULL,
    CONSTRAINT pk_usersfavourites PRIMARY KEY (id)
);
/*
CREATE TABLE revinfo
(
    rev      integer NOT NULL AUTO_INCREMENT,
    revtstmp BIGINT,
    CONSTRAINT revinfo_pkey PRIMARY KEY (rev)
);


CREATE TABLE sensor_data_aud
(
    aud_id            bigint       NOT NULL AUTO_INCREMENT,
    rev               integer      NOT NULL ,
    revtype           smallint,
    id                VARCHAR(255) NOT NULL,
    measurement       DOUBLE       NOT NULL,
    sensor_station_id VARCHAR(255) NULL,
    type              VARCHAR(255) NULL,
    unit              VARCHAR(255) NULL,
    timestamp         timestamp    NULL,
    create_date       timestamp    NOT NULL,
    CONSTRAINT sensor_data_pkey PRIMARY KEY (aud_id, rev),
    CONSTRAINT sensor_data_revinfo FOREIGN KEY (rev)
        REFERENCES revinfo (rev) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION


);



CREATE TABLE userx_user_role_aud
(
    aud_id         bigint       NOT NULL AUTO_INCREMENT,
    rev            integer      NOT NULL,
    revtype        smallint,
    userx_username VARCHAR(100) NOT NULL,
    roles          VARCHAR(255) NULL,
    CONSTRAINT userx_user_role_pkey PRIMARY KEY (aud_id, rev),
    CONSTRAINT userx_user_role_revinfo FOREIGN KEY (rev)
        REFERENCES revinfo (rev) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE userx_aud
(
    aud_id               bigint       NOT NULL AUTO_INCREMENT,
    rev                  integer      NOT NULL,
    revtype              smallint,
    username             VARCHAR(100) NOT NULL,
    create_user_username VARCHAR(100) NULL,
    create_date          timestamp    NULL,
    update_user_username VARCHAR(100) NULL,
    update_date          timestamp    NULL,
    password             VARCHAR(255) NULL,
    first_name           VARCHAR(255) NULL,
    last_name            VARCHAR(255) NULL,
    email                VARCHAR(255) NULL,
    phone                VARCHAR(255) NULL,
    enabled              BOOLEAN      NULL,
    CONSTRAINT userx_pkey PRIMARY KEY (aud_id, rev),
    CONSTRAINT userx_revinfo FOREIGN KEY (rev)
        REFERENCES revinfo (rev) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION


);

CREATE TABLE sensor_station_aud
(
    aud_id                bigint       NOT NULL AUTO_INCREMENT,
    rev                   integer      NOT NULL,
    revtype               smallint,
    id                    VARCHAR(255) NOT NULL,
    connected             BOOLEAN      NULL,
    enabled               BOOLEAN      NULL,
    dip_id                BIGINT       NULL,
    name                  VARCHAR(255) NULL,
    category              VARCHAR(255) NULL,
    transmission_interval DOUBLE       NULL,
    create_date           timestamp    NULL,
    access_point_id       VARCHAR(255) NULL,
    gardener_id           VARCHAR(255) NULL ,
    CONSTRAINT sensor_station_pkey PRIMARY KEY (aud_id, rev),
    CONSTRAINT sensor_station_revinfo FOREIGN KEY (rev)
        REFERENCES revinfo (rev) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION


);

CREATE TABLE access_point_aud
(
    aud_id            bigint       NOT NULL AUTO_INCREMENT,
    rev               integer      NOT NULL,
    revtype           smallint,
    access_point_name VARCHAR(255),
    id                VARCHAR(255) NOT NULL,
    access_Point_Role VARCHAR(100),
    create_date       timestamp    NULL,
    connected         BOOLEAN      NULL,
    enabled           BOOLEAN      NULL,
    sending_interval  DOUBLE       NULL,
    update_date       timestamp    NULL,
    password          VARCHAR(255),
    CONSTRAINT access_point_pkey PRIMARY KEY (aud_id, rev),
    CONSTRAINT access_point_revinfo FOREIGN KEY (rev)
        REFERENCES revinfo (rev) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION


);
*/
ALTER TABLE sensor_data
    ADD CONSTRAINT FK_SENSORDATA_ON_SENSORSTATION FOREIGN KEY (sensor_station_id) REFERENCES sensor_station (id) ON DELETE CASCADE;

ALTER TABLE sensor_station
    ADD CONSTRAINT FK_SENSORSTATIONGARDENER_ON_GARDENER_USERNAME FOREIGN KEY (gardener_id) REFERENCES userx (username) ON DELETE SET NULL;

ALTER TABLE sensor_station
    ADD CONSTRAINT FK_SENSORSTATION_ON_ACCESSPOINT FOREIGN KEY (access_point_id) REFERENCES access_point (id) ON DELETE CASCADE;

ALTER TABLE userx
    ADD CONSTRAINT FK_USERX_ON_CREATEUSER_USERNAME FOREIGN KEY (create_user_username) REFERENCES userx (username);

ALTER TABLE userx
    ADD CONSTRAINT FK_USERX_ON_UPDATEUSER_USERNAME FOREIGN KEY (update_user_username) REFERENCES userx (username);

ALTER TABLE userx_user_role
    ADD CONSTRAINT fk_userx_userrole_on_userx FOREIGN KEY (userx_username) REFERENCES userx (username);

ALTER TABLE users_favourites
    ADD CONSTRAINT fk_users_favourites_on_userx FOREIGN KEY (username) REFERENCES userx(username);

ALTER TABLE users_favourites
    ADD CONSTRAINT fk_users_favourites_on_sensor_station FOREIGN KEY (sensor_station_id) REFERENCES sensor_station(id);
