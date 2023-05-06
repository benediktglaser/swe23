package at.qe.g1t2.controller;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.repositories.AccessPointRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.ui.controllers.AccessPointDetailController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
public class AccessPointDetailControllerTest {

    @Mock
    AccessPointService accessPointService;

    @InjectMocks
    AccessPointDetailController accessPointDetailController;

    @Autowired
    AccessPointService accessPointService2;

    @Mock
    AccessPointRepository accessPointRepository;

    @Mock
    SensorStationRepository sensorStationRepository;


    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void editAccessPoint() {
        AccessPoint accessPoint = accessPointService2.loadAccessPoint("7269ddec-30c6-44d9-bc1f-8af18da09ed3");
        accessPointDetailController.setAccessPoint(accessPoint);
        accessPoint = accessPointDetailController.getAccessPoint();
        accessPoint.setAccessPointName("Stubai");
        accessPointDetailController.setAccessPoint(accessPoint);
        accessPointDetailController.doSaveAccessPoint(accessPoint);
        accessPointDetailController.doSaveAccessPoint();
        accessPoint = accessPointDetailController.getAccessPoint();
        Assertions.assertEquals("Stubai", accessPoint.getAccessPointName());
    }
}
