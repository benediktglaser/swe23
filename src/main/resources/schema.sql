DROP TABLE IF EXISTS sensor_data;
DROP TABLE IF EXISTS sensor_station_gardener;
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
    CONSTRAINT pk_sensorstation PRIMARY KEY (id)
);

CREATE TABLE sensor_station_gardener
(
    id                VARCHAR(255) NOT NULL,
    gardener_username VARCHAR(100) NULL,
    sensor_station_id VARCHAR(255) NULL,
    CONSTRAINT pk_sensorstationgardener PRIMARY KEY (id)
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

ALTER TABLE sensor_data
    ADD CONSTRAINT FK_SENSORDATA_ON_SENSORSTATION FOREIGN KEY (sensor_station_id) REFERENCES sensor_station (id) ON DELETE CASCADE;

ALTER TABLE sensor_station_gardener
    ADD CONSTRAINT FK_SENSORSTATIONGARDENER_ON_GARDENER_USERNAME FOREIGN KEY (gardener_username) REFERENCES userx (username) ON DELETE CASCADE;

ALTER TABLE sensor_station_gardener
    ADD CONSTRAINT FK_SENSORSTATIONGARDENER_ON_SENSORSTATION FOREIGN KEY (sensor_station_id) REFERENCES sensor_station (id) ON DELETE CASCADE;

ALTER TABLE sensor_station
    ADD CONSTRAINT FK_SENSORSTATION_ON_ACCESSPOINT FOREIGN KEY (access_point_id) REFERENCES access_point (id) ON DELETE CASCADE;

ALTER TABLE userx
    ADD CONSTRAINT FK_USERX_ON_CREATEUSER_USERNAME FOREIGN KEY (create_user_username) REFERENCES userx (username);

ALTER TABLE userx
    ADD CONSTRAINT FK_USERX_ON_UPDATEUSER_USERNAME FOREIGN KEY (update_user_username) REFERENCES userx (username);

ALTER TABLE userx_user_role
    ADD CONSTRAINT fk_userx_userrole_on_userx FOREIGN KEY (userx_username) REFERENCES userx (username);
