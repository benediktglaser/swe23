package at.qe.g1t2.RestAPI.controller;

import at.qe.g1t2.RestAPI.model.SensorStationDTO;
import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class SensorStationConnectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    SensorStationService sensorStationService;
    @Autowired
    AccessPointService accessPointService;
    @Test
    @WithMockUser(username = "43d5aba9-29c5-49b4-b4ec-2d430e34104f", authorities = {"ACCESS_POINT"})
    void createSensorStation() throws Exception {
        SensorStationDTO sensorStationDTO = new SensorStationDTO();
        sensorStationDTO.setDipId(23L);

        int size = sensorStationService.getAllSensorStations().size();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(sensorStationDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sensorStation/connect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(size + 1, sensorStationService.getAllSensorStations().size());
    }
    @Test
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testGetLimitsAndConnection() throws Exception {
        AccessPoint accessPoint = accessPointService.loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb");
        Long dipId = 3L;

        long x = Long.parseLong("100");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/limits/"+x))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Assertions.assertTrue(accessPointService.loadAccessPoint(accessPoint.getAccessPointID()).getConnected());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensorStation/limits/8"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Assertions.assertTrue(accessPointService.loadAccessPoint(accessPoint.getAccessPointID()).getConnected());
        Assertions.assertTrue(sensorStationService.loadSensorStation("fac9c9b9-62cc-4d3d-af5b-06d9965f0f7c").getConnected());

    }

}