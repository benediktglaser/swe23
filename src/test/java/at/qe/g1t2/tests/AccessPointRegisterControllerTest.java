package at.qe.g1t2.tests;

import at.qe.g1t2.RestAPI.exception.EntityNotFoundException;
import at.qe.g1t2.RestAPI.model.AccessPointDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class AccessPointRegisterControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    AccessPointService accessPointService;

    @Autowired
    SensorStationService sensorStationService;

    @Test
    void registerAccessPoint() throws Exception {
        AccessPointDTO accessPointDTO = new AccessPointDTO();
        accessPointDTO.setSendingInterval(23.0);
        accessPointDTO.setAccessPointName("TV ROOM");

        int size = accessPointService.getAllAccessPoints().size();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(accessPointDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/accessPoint/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(size + 1, accessPointService.getAllAccessPoints().size());
    }
    @Test
    @WithMockUser(username = "4294ba1b-f794-4e3d-b606-896b28237bcb", authorities = {"ACCESS_POINT"})
    void testGetLimits() throws Exception {
        AccessPoint accessPoint = accessPointService.loadAccessPoint("4294ba1b-f794-4e3d-b606-896b28237bcb");
        Long dipId = 3L;

        long x = Long.parseLong("100");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/accessPoint/register/"+x))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Assertions.assertTrue(accessPointService.loadAccessPoint(accessPoint.getAccessPointID()).getConnected(),"First");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/accessPoint/register/8"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Assertions.assertTrue(accessPointService.loadAccessPoint(accessPoint.getAccessPointID()).getConnected());
        Assertions.assertTrue(sensorStationService.loadSensorStation("fac9c9b9-62cc-4d3d-af5b-06d9965f0f7c").getConnected());

    }
}
