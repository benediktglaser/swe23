package at.qe.g1t2.services;


import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;
import java.util.UUID;


@SpringBootTest
@WebAppConfiguration
public class SensorDataServiceTest {

    @Autowired
    SensorDataService sensorDataService;

    @Autowired
    SensorStationService sensorStationService;


    @Test
    @WithMockUser(username = "elvis", authorities = {"ADMIN"})
    public void testGetAllDataBySensorStation() {
        Collection<SensorData> list = sensorDataService.getAllSensorDataByStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        Assertions.assertEquals(2, list.size());
    }

    @Test
    @WithMockUser(username = "elvis", authorities = {"ADMIN"})
    public void testGetAllSensorDataByType() {
        Collection<SensorData> list = sensorDataService.getAllSensorDataByType("Air-quality");
        Assertions.assertEquals(1, list.size());
    }

    @Test
    @WithMockUser(username = "elvis", authorities = {"ADMIN"})
    public void testLoadSensorData() {
        SensorData test = sensorDataService.loadSensorData(UUID.fromString("0718665a-5c08-4580-a4de-d8089d8756db"));
        Assertions.assertEquals("0718665a-5c08-4580-a4de-d8089d8756db", test.getId().toString());
        Assertions.assertEquals(20.34, test.getMeasurement());
        Assertions.assertEquals("C", test.getUnit());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "elvis", authorities = {"ADMIN"})
    public void testSaveNewSensorData() {
        SensorData newSensorData = new SensorData();
        newSensorData.setMeasurement(34.21);
        newSensorData.setType("Humidity");
        newSensorData.setUnit("%");
        SensorStation sensorStation = sensorStationService.loadSensorStation(UUID.fromString("9f98b70c-4de7-46c0-a611-21160743be7e"));
        int sizeOfListBefore = sensorStation.getSensorData().size();
        UUID sensorDataId = sensorDataService.saveSensorData(sensorStation, newSensorData).getId();
        Assertions.assertEquals(sizeOfListBefore + 1, sensorStationService.loadSensorStation(sensorStation.getId()).getSensorData().size());
        Assertions.assertEquals(sensorStation.getId(), sensorDataService.loadSensorData(sensorDataId).getSensorStation().getId());
    }


}
