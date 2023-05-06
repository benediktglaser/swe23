package at.qe.g1t2.restapi.controller;


import at.qe.g1t2.services.QRCodeService;
import at.qe.g1t2.services.SensorStationService;
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

@SpringBootTest
@AutoConfigureMockMvc
public class QRCodeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SensorStationService sensorStationService;

    @Autowired
    private QRCodeService qrCodeService;


    /*
    @Test
    @DirtiesContext
    @WithMockUser(username = "elvis", authorities = {"GARDENER"})
    public void testGetQRCode() throws Exception {

    }
     */

}
