package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure correct behavior of the methods. Especially for correct interacting with
 * the database
 */
@SpringBootTest
class SensorStationServiceTest {

    @Autowired
    SensorStationService sensorStationService;
    @Autowired
    UserService userService;
    @Autowired
    AccessPointService accessPointService;

    @Autowired
    UserxRepository userxRepository;

    @Autowired
    SensorStationRepository sensorStationRepository;


    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void saveNewSensorStation() {
        AccessPoint accessPoint = accessPointService
                .loadAccessPoint("7269ddec-30c6-44d9-bc1f-8af18da09ed3");
        SensorStation sensorStation = new SensorStation();


        sensorStation = sensorStationService.saveSensorStation(accessPoint, sensorStation);

        assertEquals(accessPoint, sensorStation.getAccessPoint());

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteSensorStation() {
        SensorStation sensorStation = sensorStationService
                .loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        sensorStationService.deleteSensorStation(sensorStation);
        SensorStation deletedSensorStation = sensorStationRepository
                .findSensorStationById(sensorStation.getId());

        assertNull(deletedSensorStation);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void removeSensorStation() {
        AccessPoint accessPoint = accessPointService
                .loadAccessPoint("7269ddec-30c6-44d9-bc1f-8af18da09ed3");
        SensorStation sensorStation = sensorStationService
                .loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");

        sensorStationService.removeSensorStationFromAccessPoint(accessPoint, sensorStation);
        sensorStation = sensorStationService
                .loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");

        assertNull(sensorStation);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loadSensorStation() {
        SensorStation sensorStation = sensorStationService
                .loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        assertEquals(sensorStation.getId(), "8ccfdfaa-9731-4786-8efa-e2141e5c4095");
    }
}