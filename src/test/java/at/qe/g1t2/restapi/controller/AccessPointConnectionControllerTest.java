package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
@AutoConfigureMockMvc
class AccessPointConnectionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    AccessPointService accessPointService;
    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;

    @Autowired
    SensorStationService sensorStationService;
    @Test
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void checkIfAccessPointIsConnectedAndSendInterval() throws Exception {
        AccessPoint accessPoint = accessPointService.loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accessPoint/interval"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> assertEquals(3.0, Double.parseDouble(result.getResponse().getContentAsString()), 0.0));
        Assertions.assertTrue(accessPointService.loadAccessPoint(accessPoint.getAccessPointID()).getConnected());

    }

    @Test
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void switchToCoupleMode() throws Exception {
        AccessPoint accessPoint = accessPointService.loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb");
        accessPoint.setCoupleMode(true);

        accessPointService.saveAccessPoint(accessPoint);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accessPoint/couple"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> assertTrue(Boolean.parseBoolean(result.getResponse().getContentAsString())));
        Assertions.assertTrue(accessPointService.loadAccessPoint(accessPoint.getAccessPointID()).getConnected());
        Assertions.assertTrue(accessPointService.loadAccessPoint(accessPoint.getAccessPointID()).getCoupleMode());
    }

}