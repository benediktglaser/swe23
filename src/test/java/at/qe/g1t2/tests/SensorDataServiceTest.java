package at.qe.g1t2.tests;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@WebAppConfiguration
class SensorDataServiceTest {
    @Autowired
    SensorDataService sensorDataService;
    @Autowired
    SensorStationService sensorStationService;

    @Test
    @WithMockUser(username = "7269ddec-30c6-44d9-bc1f-8af18da09ed3", authorities = {"ACCESS_POINT"})
    void getAllNewSensorDataByStationAndTypeForChart() {
        SensorStation sensorStation =   sensorStationService.loadSensorStation("fac9c9b9-62cc-4d3d-af5b-06d9965f0f7c");
        System.out.println(sensorDataService.getAllNewSensorDataByStationAndTypeForChart(sensorStation, SensorDataType.TEMPERATURE, LocalDateTime.of(2020,1,1,0,0,0)));
    }
}