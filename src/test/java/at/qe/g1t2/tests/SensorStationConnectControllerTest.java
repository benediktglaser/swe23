package at.qe.g1t2.tests;

import at.qe.g1t2.RestAPI.model.SensorStationDTO;
import at.qe.g1t2.services.SensorStationService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Test
    @WithMockUser(username = "7269ddec-30c6-44d9-bc1f-8af18da09ed3", authorities = {"ACCESS_POINT"})
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
}