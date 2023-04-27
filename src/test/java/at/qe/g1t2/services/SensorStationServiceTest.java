package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
                .loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb");
        SensorStation sensorStation = new SensorStation();
        sensorStation.setDipId(23L);
        sensorStation.setMac("45667");

        sensorStation = sensorStationService.saveSensorStation(accessPoint, sensorStation);

        assertEquals(accessPoint, sensorStation.getAccessPoint());

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteSensorStation() {
        SensorStation sensorStation = sensorStationService
                .loadSensorStation("b94d9ff0-1366-49b1-b19b-85f73c14d744");
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
                .loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb");
        SensorStation sensorStation = sensorStationService
                .loadSensorStation("9f98b70c-4de7-46c0-a611-21160743be7e");

        sensorStationService.removeSensorStationFromAccessPoint(accessPoint, sensorStation);
        sensorStation = sensorStationService
                .loadSensorStation("9f98b70c-4de7-46c0-a611-21160743be7e");

        assertNull(sensorStation);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loadSensorStation() {
        SensorStation sensorStation = sensorStationService
                .loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        assertEquals("8ccfdfaa-9731-4786-8efa-e2141e5c4095", sensorStation.getId());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void testGetAllNewSensorStations() {
        AccessPoint accessPoint = accessPointService.loadAccessPoint("43d5aba9-29c5-49b4-b4ec-2d430e34104f");
        int size = sensorStationService.getAllNewSensorStations(accessPoint).size();
        Assertions.assertEquals(3, size);
    }

}