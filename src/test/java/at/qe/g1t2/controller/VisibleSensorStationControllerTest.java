package at.qe.g1t2.controller;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.restapi.model.SensorStationDTO;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.VisibleSensorStationsService;
import at.qe.g1t2.ui.controllers.VisibleSensorStationController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class VisibleSensorStationControllerTest {
    @Mock
    private VisibleSensorStationsService visibleSensorStationsService;

    @InjectMocks
    private VisibleSensorStationController visibleSensorStationController;


    @Autowired
    private AccessPointService accessPointService;
    @Test
    void testGetVisibleMap() {
        Map<AccessPoint, Map<Long, SensorStationDTO>> mockVisibleMap = new HashMap<>();
        AccessPoint accessPoint = accessPointService.loadAccessPoint("43d5aba9-29c5-49b4-b4ec-2d430e34104f");

        when(visibleSensorStationsService.getVisibleMap()).thenReturn(mockVisibleMap);
        Map<AccessPoint, Map<Long, SensorStationDTO>> visibleMap = visibleSensorStationController.getVisibleMap();
        assertEquals(mockVisibleMap, visibleMap);
    }


}
