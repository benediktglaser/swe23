package at.qe.g1t2.controller;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.SensorStationGardenerService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.UserService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import at.qe.g1t2.ui.controllers.GardenerSensorStationController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;


@SpringBootTest
public class GardenerSensorStationControllerTest {

    @InjectMocks
    private GardenerSensorStationController gardenerSensorStationController;

    @Mock
    private SensorStationService sensorStationService;

    @Mock
    SessionSensorStationBean sessionSensorStationBean;

    @Mock
    private SensorStationGardenerService sensorStationGardenerService;

    @Mock
    private UserService userService;

    @Test
    public void testSettingUsers() {
        gardenerSensorStationController.setUsername("user1");
        Assertions.assertEquals("user1", gardenerSensorStationController.getUsername());
    }
/*
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testAssignGardener() {
        Userx user = userService.loadUser("petersen");
        gardenerSensorStationController.setUsername("petersen");
        SensorStation station = sensorStationService.loadSensorStation("cf6d8d3e-9b9c-4172-98d8-50b29f1e1f87");

        gardenerSensorStationController.assignSensorStationToGardener(station);
        station = sensorStationService.loadSensorStation("cf6d8d3e-9b9c-4172-98d8-50b29f1e1f87");
        Assertions.assertEquals(user, station.getGardener());
    }
    
 */


}