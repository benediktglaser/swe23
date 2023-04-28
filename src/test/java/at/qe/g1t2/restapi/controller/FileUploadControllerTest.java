package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.Picture;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.restapi.model.SensorStationDTO;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
class FileUploadControllerTest {

    @Autowired
    AccessPointService accessPointService;
    @Autowired
    SensorStationService sensorStationService;
    private final HttpServletRequest requestMock = new MockHttpServletRequest();
    private final RedirectAttributes redirectAttributesMock = mock(RedirectAttributes.class);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FileUploadController fileUploadController;

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testHandleFileUpload() throws Exception {
        SensorStation sensorStation = sensorStationService.loadSensorStation("c57004cc-ca72-41a0-9432-754e642696cb");

        String fileName = "test.jpg";
        String contentType = "image/jpeg";
        byte[] content = {0x01, 0x02, 0x03};

        MultipartFile file = new MockMultipartFile("file", fileName, contentType, content);

        ResponseEntity<Picture> responseEntity = fileUploadController.handleFileUpload(file, sensorStation.getId(), requestMock, redirectAttributesMock);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


}