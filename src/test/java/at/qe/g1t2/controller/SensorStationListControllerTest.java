package at.qe.g1t2.controller;

import at.qe.g1t2.ui.controllers.SensorStationListController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationGardenerService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.UserService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
class SensorStationListControllerTest {

    @Autowired
    private SensorStationService sensorStationService;

    @Mock
    private SensorStationGardenerService sensorStationGardenerService;

    @Mock
    private SensorDataService sensorDataService;


    @Mock
    private SessionSensorStationBean sessionSensorStationBean;

    @Mock
    private UserService userService;

    @Mock
    private AccessPointService accessPointService;

    @InjectMocks
    private SensorStationListController sensorStationListController;

    /*
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

     */

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void testGetFrontPicture() throws Exception {
        SensorStation sensorStation = sensorStationService.loadSensorStation("e6a456c1-ef03-4c62-8e80-1e7bba2a03a9");
        Assertions.assertEquals("plant-test1.png", sensorStationListController.getFrontPicture(sensorStation));
    }


    @Test
    void testRedirectToSensorDataPage() {
        SensorStation sensorStation = sensorStationService.loadSensorStation("e6a456c1-ef03-4c62-8e80-1e7bba2a03a9");
        Assertions.assertEquals("sensorData.xhtml?faces-redirect=true", sensorStationListController.redirectToSensorDataPage(sensorStation));
    }

    @Test
    void testRedirectToGallery() {
        SensorStation sensorStation = sensorStationService.loadSensorStation("a1a96ebd-e6b1-426e-87b1-9c9f72118e05");
        Assertions.assertEquals("gallery.xhtml?faces-redirect=true", sensorStationListController.redirectToGallery(sensorStation));
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"GARDENER"})
    void testGetAllSensorStationsByOwner() {
        int size = sensorStationListController.getAllSensorStationByOwner().size();
        Assertions.assertEquals(0, size);
    }


}