package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.repositories.SensorDataRepository;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@SpringBootTest
@AutoConfigureMockMvc
public class ChartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    SensorDataService sensorDataService;

    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    SensorDataRepository sensorDataRepository;

    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;


    @Test
    @DirtiesContext
    @WithMockUser(username = "elvis", authorities = {"GARDENER"})
    void testGetSensorData() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/chart")
                        .param("sensorStationId", "b94d9ff0-1366-49b1-b19b-85f73c14d744")
                        .param("sensorDataTypeId", "id3cc")
                        .param("typeId", "TEMPERATURE"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals("[{\"data\":\"[[[2010,3,23,14,54],22.0]]\",\"min\":-20.0,\"max\":80.0}]", result.getResponse().getContentAsString());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "elvis", authorities = {"GARDENER"})
    void testGetNewSensorData() throws Exception {
        LocalDateTime lastDate = LocalDateTime.of(2010, 1, 23, 12, 4, 6);
        long timestamp = lastDate.toEpochSecond(ZoneOffset.UTC) * 1000L;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/chart/add")
                        .param("sensorStationId", "b94d9ff0-1366-49b1-b19b-85f73c14d744")
                        .param("sensorDataTypeId", "id3cc")
                        .param("typeId", "TEMPERATURE")
                        .param("lastDate", Long.toString(timestamp)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals("[{\"data\":\"[[[2010,3,23,14,54],22.0]]\"}]", result.getResponse().getContentAsString());
    }


    @Test
    @DirtiesContext
    @WithMockUser(username = "elvis", authorities = {"GARDENER"})
    void testGetNewSensorDataEmpty() throws Exception {
        LocalDateTime lastDate = LocalDateTime.of(2023, 1, 23, 12, 4, 6);
        long timestamp = lastDate.toEpochSecond(ZoneOffset.UTC) * 1000L;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/chart/add")
                        .param("sensorStationId", "b94d9ff0-1366-49b1-b19b-85f73c14d744")
                        .param("sensorDataTypeId", "id3cc")
                        .param("typeId", "TEMPERATURE")
                        .param("lastDate", Long.toString(timestamp)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertEquals("[{\"data\":\"[]\"}]", result.getResponse().getContentAsString());
    }
}
