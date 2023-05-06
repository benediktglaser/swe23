package at.qe.g1t2.controller;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.ui.controllers.SensorDataTypeInfoController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SensorDataTypeInfoControllerTest {
    @Mock
    private SensorDataTypeInfoController controller;

    @Mock
    SensorStation sensorStation;

    @Mock
    private SensorDataTypeInfoService sensorDataTypeInfoService;

    @Mock
    private SensorStationService sensorStationService;

    @Test
    public void testGetType() {
        SensorDataTypeInfoController controller = new SensorDataTypeInfoController();

        assertNotNull(controller.getType());
        assertNotNull(controller.getType());
        assertNotNull(controller.getType());

        SensorDataTypeInfo type = controller.getType();
        type.setMaxLimit(100.0);
        type.setMinLimit(0.0);

        SensorDataTypeInfo type2 = controller.getType();
        assertNotNull(type2);
        assertEquals(100.0, type2.getMaxLimit());
        assertEquals(0.0, type2.getMinLimit());
    }

    @Test
    public void testGetterSetter(){
        SensorDataTypeInfoController controller = new SensorDataTypeInfoController();
        controller.setSensorDataType(SensorDataType.TEMPERATURE);
        Assertions.assertEquals(SensorDataType.TEMPERATURE,controller.getSensorDataType());
    }

}