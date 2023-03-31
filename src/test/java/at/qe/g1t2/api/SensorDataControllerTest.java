package at.qe.g1t2.api;


import at.qe.g1t2.api.controller.SensorDataController;
import at.qe.g1t2.repositories.SensorDataRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import javax.sql.DataSource;

@WebMvcTest(SensorDataController.class)
public class SensorDataControllerTest {

    @MockBean
    SensorStationService sensorStationService;

    @MockBean
    SensorDataService sensorDataService;

    @MockBean
    SensorStationRepository sensorStationRepository;

    @MockBean
    SensorDataRepository sensorDataRepository;

    @MockBean
    @Autowired
    DataSource dataSource;



    @Test
    public void testPostMapping() throws Exception {

    }

}
