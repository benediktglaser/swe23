package at.qe.g1t2.restapi.controller;


import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.restapi.model.SensorDataDTO;
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

    @Autowired
    SensorStationRepository sensorStationRepository;


    @Test
    @WithMockUser(username = "7269ddec-30c6-44d9-bc1f-8af18da09ed3", authorities = {"ACCESS_POINT"})
    public void testPostMapping() throws Exception {
        SensorDataDTO sensorDataDTO = new SensorDataDTO();
        sensorDataDTO.setMeasurement(0.9);
        sensorDataDTO.setTimestamp(LocalDateTime.now());
        sensorDataDTO.setType(SensorDataType.TEMPERATURE);
        sensorDataDTO.setDipId(1L);

        int size = sensorDataService.getAllSensorDataByStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095").size();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String requestBody = objectMapper.writeValueAsString(sensorDataDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/sensorData").contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(size + 1, sensorDataService.getAllSensorDataByStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095").size());


    }

}
