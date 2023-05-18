package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.restapi.model.AccessPointDTO;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        accessPointDTO.setSendingInterval(40.0);
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
    void checkCredentials() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/accessPoint/register/credentials?accessPointId=asdasd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertEquals("false", result.getResponse().getContentAsString());
    }


    @Test
    void checkCredentialsWithRealAccessPoint() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/accessPoint/register/credentials?accessPointId=7269ddec-30c6-44d9-bc1f-8af18da09ed3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertEquals("true", result.getResponse().getContentAsString());
    }

}
