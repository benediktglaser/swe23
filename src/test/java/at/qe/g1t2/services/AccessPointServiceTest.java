package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AccessPointServiceTest {
    @Autowired
    AccessPointService accessPointService;
    @Autowired
    SensorStationService sensorStationService;

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loadAccessPoint() {

        AccessPoint accessPoint = accessPointService.loadAccessPoint(UUID.fromString("7269ddec-30c6-44d9-bc1f-8af18da09ed3"));
        assertEquals(accessPoint.getAccessPointID(), UUID.fromString("7269ddec-30c6-44d9-bc1f-8af18da09ed3"));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void saveAccessPoint() {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setAccessPointName("Test");
        accessPoint = accessPointService.saveAccessPoint(accessPoint);
        LocalDateTime dateTime = accessPoint.getCreateDate();
        assertEquals(accessPoint, accessPointService.loadAccessPoint(accessPoint.getAccessPointID()));
        accessPoint = accessPointService.saveAccessPoint(accessPoint);
        assertEquals(accessPoint.getCreateDate(), dateTime);

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteAccessPoint() {
        AccessPoint accessPoint = accessPointService
                .loadAccessPoint(UUID.fromString("7269ddec-30c6-44d9-bc1f-8af18da09ed3"));
        SensorStation sensorStation = sensorStationService
                .loadSensorStation(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));
        sensorStation.setAccessPoint(accessPoint);
        sensorStationService.saveSensorStation(sensorStation);
        accessPointService.deleteAccessPoint(accessPoint);

        assertNull(accessPointService
                .loadAccessPoint(UUID.fromString("7269ddec-30c6-44d9-bc1f-8af18da09ed3")));

        sensorStation = sensorStationService
                .loadSensorStation(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));
        assertNull(sensorStation.getAccessPoint());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void addSensorStation() {
        AccessPoint accessPoint = accessPointService
                .loadAccessPoint(UUID.fromString("7269ddec-30c6-44d9-bc1f-8af18da09ed3"));
        SensorStation sensorStation = sensorStationService
                .loadSensorStation(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));

        accessPointService.addSensorStation(accessPoint,sensorStation);
        sensorStation = sensorStationService
                .loadSensorStation(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));

        assertTrue(sensorStation.getConnected());
        assertEquals(sensorStation.getAccessPoint(),accessPoint);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void removeSensorStation() {
        AccessPoint accessPoint = accessPointService
                .loadAccessPoint(UUID.fromString("7269ddec-30c6-44d9-bc1f-8af18da09ed3"));
        SensorStation sensorStation = sensorStationService
                .loadSensorStation(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));

        accessPointService.removeSensorStation(accessPoint,sensorStation);
        sensorStation = sensorStationService
                .loadSensorStation(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));

        assertFalse(sensorStation.getConnected());
        assertNull(sensorStation.getAccessPoint());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllSensorStations() {
        AccessPoint accessPoint = accessPointService
                .loadAccessPoint(UUID.fromString("7269ddec-30c6-44d9-bc1f-8af18da09ed3"));
        SensorStation sensorStation = sensorStationService
                .loadSensorStation(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));
        SensorStation sensorStation2 = sensorStationService
                .loadSensorStation(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));
        accessPointService.addSensorStation(accessPoint,sensorStation);
        accessPointService.addSensorStation(accessPoint,sensorStation2);

        assertEquals(2,accessPointService.getAllSensorStations(accessPoint).size());

    }
}