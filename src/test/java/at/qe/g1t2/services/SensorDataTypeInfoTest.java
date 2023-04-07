package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorDataTypeInfoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
public class SensorDataTypeInfoTest {
    @Autowired
    private SensorStationService sensorStationService;

    @Autowired
    private SensorDataTypeInfoService sensorDataTypeInfoService;

    @Autowired
    private SensorDataTypeInfoRepository sensorDataTypeInfoRepository;

    @Test
    @DirtiesContext
    @WithMockUser(username = "user1", authorities = {"GARDENER"})
    void sensorDataTypeSaveTest(){
        SensorStation sensorStation= sensorStationService.loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        SensorDataTypeInfo sensorDataTypeTemp1=new SensorDataTypeInfo();
        sensorDataTypeTemp1.setType(SensorDataType.TEMPERATURE);
        SensorDataTypeInfo sensorDataTypeTemp2=new SensorDataTypeInfo();
        sensorDataTypeTemp2.setType(SensorDataType.TEMPERATURE);
        SensorDataTypeInfo sensorDataTypeGas1=new SensorDataTypeInfo();
        sensorDataTypeGas1.setType(SensorDataType.GAS);
        SensorDataTypeInfo sensorDataTypeGas2=new SensorDataTypeInfo();
        sensorDataTypeGas2.setType(SensorDataType.GAS);
        sensorDataTypeInfoService.save(sensorStation,sensorDataTypeTemp1);
        sensorDataTypeInfoService.getAllSensorDataTypeInfosBySensorStation(sensorStation);
        sensorStation= sensorStationService.loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        System.out.println(sensorStation.getSensorDataTypeInfos().size());
        Assertions.assertEquals(2,sensorStation.getSensorDataTypeInfos().size());

        sensorDataTypeInfoService.save(sensorStation,sensorDataTypeTemp2);
        sensorDataTypeInfoService.getAllSensorDataTypeInfosBySensorStation(sensorStation);
        sensorStation= sensorStationService.loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        System.out.println(sensorStation.getSensorDataTypeInfos().size());
        Assertions.assertEquals(3,sensorStation.getSensorDataTypeInfos().size());

        sensorDataTypeInfoService.save(sensorStation,sensorDataTypeGas1);
        sensorDataTypeInfoService.getAllSensorDataTypeInfosBySensorStation(sensorStation);
        sensorStation= sensorStationService.loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        System.out.println(sensorStation.getSensorDataTypeInfos().size());
        Assertions.assertEquals(4, sensorStation.getSensorDataTypeInfos().size());

        sensorDataTypeInfoService.save(sensorStation,sensorDataTypeGas2);
        sensorDataTypeInfoService.getAllSensorDataTypeInfosBySensorStation(sensorStation);
        sensorStation= sensorStationService.loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        System.out.println(sensorStation.getSensorDataTypeInfos().size());
        Assertions.assertEquals(5, sensorStation.getSensorDataTypeInfos().size());



    }
}
