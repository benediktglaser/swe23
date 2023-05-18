package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import jakarta.faces.model.SelectItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;

@SpringBootTest
@WebAppConfiguration
class ChartsViewTest {
    @Mock
    private SensorDataTypeInfoService sensorDataTypeInfoService;
    @Autowired
    private SensorStationService sensorStationService;

    @Mock
    private SessionSensorStationBean sessionSensorStationBean;
    @Mock
    private PrimeFaces primeFaces;
    @InjectMocks
    private ChartsView chartsView;


    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void init() {
        chartsView.init();
        sessionSensorStationBean.setSensorStation(sensorStationService.loadSensorStation("fac9c9b9-62cc-4d3d-af5b-06d9965f0f7c"));
        boolean setUpCorrect = false;
        for (SelectItem selectItem : chartsView.getCategories()) {
            if (Arrays.stream(SensorDataType.values()).toList().contains(SensorDataType.valueOf(selectItem.getLabel()))) {
                setUpCorrect = true;
            }
        }
        Assertions.assertTrue(setUpCorrect);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @DirtiesContext
    void convertSelection() {
        chartsView.init();
        sessionSensorStationBean.setSensorStation(sensorStationService.loadSensorStation("fac9c9b9-62cc-4d3d-af5b-06d9965f0f7c"));
        chartsView.setSelection("id14c");
        Assertions.assertEquals(sensorDataTypeInfoService.loadSensorDataTypeInfo("id14c"), chartsView.convertSelection());
        Assertions.assertEquals("id14c", chartsView.getSelection());
    }


}