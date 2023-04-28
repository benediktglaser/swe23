package at.qe.g1t2.restapi.controller;


import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.PictureService;
import at.qe.g1t2.services.SensorStationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class FileUploadControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private SensorStationService sensorStationService;


    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testHandleFileUploadSuccess() throws Exception {

        String sensorStationId = "fac9c9b9-62cc-4d3d-af5b-06d9965f0f7c";

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
                "test data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file(file)
                        .param("sensorStationId", sensorStationId))
                .andExpect(status().isOk());
        SensorStation sensorStation = sensorStationService.loadSensorStation(sensorStationId);
        Assertions.assertEquals(1, sensorStation.getPictures().size());
        Assertions.assertEquals("test.jpg", sensorStation.getPictures().get(0).getPictureName());

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testHandleFileUploadEmptyFile() throws Exception {

        String sensorStationId = "fac9c9b9-62cc-4d3d-af5b-06d9965f0f7c";

        MockMultipartFile file = new MockMultipartFile("file", "", MediaType.IMAGE_JPEG_VALUE,
                "".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file(file)
                        .param("sensorStationId", sensorStationId))
                .andExpect(status().isExpectationFailed());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testHandleFileUploadNoFileName() throws Exception {
        String sensorStationId = "fac9c9b9-62cc-4d3d-af5b-06d9965f0f7c";

        MockMultipartFile file = new MockMultipartFile("file",null , MediaType.IMAGE_JPEG_VALUE,
                "test data".getBytes());


        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file(file)
                        .param("sensorStationId", sensorStationId))
                .andExpect(status().isBadRequest());
    }

}