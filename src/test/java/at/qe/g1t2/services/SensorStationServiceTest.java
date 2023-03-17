package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorStationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

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
    AccessPointService accessPointService;

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
        UUID sensorId = sensorStationService.saveSensorStation(accessPoint,sensorStation);
        assertEquals(sensorStationRepository.findSensorStationById(sensorId).getName(),"wheat");
        assertEquals(accessPoint.getSensorStations().size(),sizeOfList+1);
        assertEquals(accessPoint.getId(),sensorStationRepository.findSensorStationById(sensorId).getAccessPoint().getAccessPointID());


    }

}