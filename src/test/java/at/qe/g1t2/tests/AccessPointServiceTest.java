package at.qe.g1t2.tests;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

        AccessPoint accessPoint = accessPointService.loadAccessPoint("7269ddec-30c6-44d9-bc1f-8af18da09ed3");
        assertEquals("7269ddec-30c6-44d9-bc1f-8af18da09ed3", accessPoint.getAccessPointID());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void saveAccessPoint() {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setAccessPointName("Test");
        accessPoint.setAccessPointID(UUID.randomUUID().toString());
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
                .loadAccessPoint("43d5aba9-29c5-49b4-b4ec-2d430e34104f");
        SensorStation sensorStation = sensorStationService
                .loadSensorStation("b94d9ff0-1366-49b1-b19b-85f73c14d744");
        sensorStation.setAccessPoint(accessPoint);
        accessPointService.deleteAccessPoint(accessPoint);

        assertNull(accessPointService
                .loadAccessPoint("43d5aba9-29c5-49b4-b4ec-2d430e34104f"));

        sensorStation = sensorStationService
                .loadSensorStation("b94d9ff0-1366-49b1-b19b-85f73c14d744");
        assertNull(sensorStation);
    }


    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllSensorStations() {
        AccessPoint accessPoint = accessPointService
                .loadAccessPoint("7269ddec-30c6-44d9-bc1f-8af18da09ed3");
        SensorStation sensorStation = sensorStationService
                .loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        SensorStation sensorStation2 = sensorStationService
                .loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        sensorStationService.saveSensorStation(accessPoint, sensorStation);
        sensorStationService.saveSensorStation(accessPoint, sensorStation2);

        assertEquals(7, accessPointService.getAllSensorStationsByAccessPoint(accessPoint).size());

    }
}