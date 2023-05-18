package at.qe.g1t2.restapi.controller;


import at.qe.g1t2.services.QRCodeService;
import at.qe.g1t2.services.SensorStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
