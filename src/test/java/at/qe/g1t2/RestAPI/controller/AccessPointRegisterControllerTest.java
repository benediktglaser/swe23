package at.qe.g1t2.RestAPI.controller;

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

}
