package at.qe.g1t2.controller;

import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.ui.controllers.SensorDataTypeListController;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class SensorDataTypeListControllerTest {

    @Mock
    private SensorDataTypeListController controller;

    @Mock
    private SensorDataTypeInfoService sensorDataTypeInfoService;

    @Mock
    private SensorStationService sensorStationService;

    @Mock
    private SensorDataTypeListController sensorDataTypeListController;

    /*
    @Test
    public void testGetAllSensorDataTypeBySensorStation() {
        SensorStation station = new SensorStation();
        station.setId("UCCC445");
        List<SensorDataTypeInfo> expectedSensorDataTypes = new ArrayList<>();
        SensorDataTypeInfo dataType1 = new SensorDataTypeInfo();
        dataType1.setId("lcc2");
        dataType1.setType(SensorDataType.TEMPERATURE);
        SensorDataTypeInfo dataType2 = new SensorDataTypeInfo();
        dataType2.setId("lcc3");
        dataType2.setType(SensorDataType.AIRQUALITY);
        expectedSensorDataTypes.add(dataType1);
        expectedSensorDataTypes.add(dataType2);
        when(sensorDataTypeInfoService.getAllSensorDataTypeInfosBySensorStation(station)).thenReturn(expectedSensorDataTypes);
        station = sensorStationService.loadSensorStation("cf6d8d3e-9b9c-4172-98d8-50b29f1e1f87");
        controller.setSensorStation(station);
        List<SensorDataTypeInfo> actualSensorDataTypes = controller.getAllSensorDataTypeBySensorStation();
        System.out.println(controller.getSensorStation().getId());
        assertEquals(expectedSensorDataTypes, actualSensorDataTypes);
    }

     */
}