package at.qe.g1t2.services;

import at.qe.g1t2.model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SensorStationGardenerServiceTest {
    @Autowired
    SensorStationService sensorStationService;
    @Autowired
    UserService userService;
    @Autowired
    SensorStationGardenerService sensorStationGardenerService;

    @Test
    void loadSensorStationGardener() {
        SensorStationGardener sensorStationGardener = sensorStationGardenerService.loadSensorStationGardener(UUID.fromString("0e5006f9-12f2-4f08-841f-6d937769eb6e"));
        assertEquals("user1",sensorStationGardener.getGardener().getUsername());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void save() {
        SensorStation sensorStation = new SensorStation();
        sensorStation.setName("wheat");
        sensorStation.setCategory("test");
        sensorStation = sensorStationService.saveSensorStation(sensorStation);
        Userx gardener = userService.loadUser("user1");

        SensorStationGardener sensorStationGardener = new SensorStationGardener();
        sensorStationGardener = sensorStationGardenerService.save(gardener,sensorStation,sensorStationGardener);
        sensorStationGardener = sensorStationGardenerService.loadSensorStationGardener(sensorStationGardener.getId());
        assertNotNull(sensorStationGardener);
        assertEquals(gardener,sensorStationGardener.getGardener());


    }
}