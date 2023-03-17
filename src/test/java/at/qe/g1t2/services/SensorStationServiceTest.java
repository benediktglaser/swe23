package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.util.UUID;

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

    /**
     * Test if Entity is saved correctly in database and in the list from the parent entity
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void saveNewSensorStation() {
        SensorStation sensorStation = new SensorStation();
        sensorStation.setName("wheat");
        sensorStation.setCategory("test");
        AccessPoint accessPoint = accessPointService.loadAccessPoint(UUID.fromString("7269ddec-30c6-44d9-bc1f-8af18da09ed3"));
        int sizeOfList = accessPoint.getSensorStations().size();
        UUID sensorId = sensorStationService.saveSensorStation(accessPoint,sensorStation).getId();
        assertEquals(sensorStationRepository.findSensorStationById(sensorId).getName(),"wheat");
        assertEquals(accessPoint.getSensorStations().size(),sizeOfList+1);
        assertEquals(accessPoint.getId(),sensorStationRepository.findSensorStationById(sensorId).getAccessPoint().getAccessPointID());


    }
    /**
     * Test if Entity is deleted correctly in database and in the list from the parent entity
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteSensorStation() {
        AccessPoint accessPoint = accessPointService
                .loadAccessPoint(UUID.fromString("7269ddec-30c6-44d9-bc1f-8af18da09ed3"));
        int sizeOfList = accessPoint.getSensorStations().size();
        SensorStation sensorStation = accessPoint.getSensorStations().get(0);
        sensorStationService.deleteSensorStation(sensorStation);
        SensorStation deletedSensorStation = sensorStationRepository
                .findSensorStationById(sensorStation.getId());

        assertEquals(accessPoint.getSensorStations().size(),sizeOfList-1);
        assertNull(deletedSensorStation);
    }

    /**
     * Test if SensorStation is still in Database when the gardener is removed
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void removeGardener() {
        Userx gardener = new Userx();
        gardener.setUsername("Gardener");
        gardener = userService.saveUser(gardener);
        SensorStation sensorStation = sensorStationRepository
                .findSensorStationById(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));

        sensorStation.setGardener(gardener);
        sensorStationService.saveSensorStation(sensorStation.getAccessPoint(),sensorStation);

        sensorStationService.removeGardener(sensorStation);

        sensorStation = sensorStationRepository
                .findSensorStationById(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));

        assertNull(sensorStation.getGardener());

    }
    /**
     * Test if gardener is replaced correctly
     */
    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void replaceGardener() {
        Userx gardener = new Userx();
        gardener.setUsername("Gardener");
        gardener = userService.saveUser(gardener);
        Userx gardener2 = new Userx();
        gardener2.setUsername("Gardener2");
        gardener = userService.saveUser(gardener);
        gardener2 = userService.saveUser(gardener2);
        SensorStation sensorStation = sensorStationRepository
                .findSensorStationById(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));

        sensorStation.setGardener(gardener);
        sensorStationService.saveSensorStation(sensorStation.getAccessPoint(),sensorStation);

        sensorStationService.replaceGardener(sensorStation,gardener2);

        sensorStation = sensorStationRepository
                .findSensorStationById(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));

        assertEquals(sensorStation.getGardener(), gardener2);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loadSensorStation() {
        SensorStation sensorStation = sensorStationService
                .loadSensorStation(UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));
        assertEquals(sensorStation.getId(),UUID.fromString("8ccfdfaa-9731-4786-8efa-e2141e5c4095"));
    }
}