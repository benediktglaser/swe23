package at.qe.g1t2.tests;

import at.qe.g1t2.RestAPI.Controller.AccessConnectionPointController;
import at.qe.g1t2.RestAPI.model.AccessPointDTO;
import at.qe.g1t2.services.AccessPointService;
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
class AccessConnectionPointControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private AccessPointService accessPointService;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createAccessPoint() throws Exception {
        AccessPointDTO accessPointDTO = new AccessPointDTO();
        accessPointDTO.setAccessPointName("Ha");
        accessPointDTO.setIntervall(23.1);
        int size = accessPointService.getAllAccessPoints().size();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(accessPointDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/accessPoint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(size+1,accessPointService.getAllAccessPoints().size());
    }
}