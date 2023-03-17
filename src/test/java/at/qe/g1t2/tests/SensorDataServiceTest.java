package at.qe.g1t2.tests;


import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataService;
import nl.jqno.equalsverifier.internal.util.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;


@SpringBootTest
@WebAppConfiguration
public class SensorDataServiceTest {

    @Autowired
    SensorDataService sensorDataService;


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetAllDataBySensorStation() {
        Collection<SensorData> list = sensorDataService.getAllSensorDataByStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        Assertions.assertEquals(2, list.size());
    }


}
