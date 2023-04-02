package at.qe.g1t2.RestAPI;


import at.qe.g1t2.RestAPI.model.SensorDataTransfer;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDateTime;


@SpringBootTest
@AutoConfigureMockMvc
public class SensorDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    SensorDataService sensorDataService;


    @Autowired
    SensorStationService sensorStationService;


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testPostMapping() throws Exception {
        SensorDataTransfer sensorDataTransfer = new SensorDataTransfer();
        sensorDataTransfer.setSensorStation("f11c3324-125f-4b2d-8b82-3692b0772d95");
        sensorDataTransfer.setMeasurement(0.9);
        sensorDataTransfer.setTimestamp(LocalDateTime.now());
        sensorDataTransfer.setType("Temperature");
        sensorDataTransfer.setUnit("Fahrenheit");

        int size = sensorDataService.getAllSensorDataByStation("f11c3324-125f-4b2d-8b82-3692b0772d95").size();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String requestBody = objectMapper.writeValueAsString(sensorDataTransfer);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/sensorData").contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertEquals(size + 1, sensorDataService.getAllSensorDataByStation("f11c3324-125f-4b2d-8b82-3692b0772d95").size());


    }

}
