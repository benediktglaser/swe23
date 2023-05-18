package at.qe.g1t2.tests;


import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.QRCodeService;
import at.qe.g1t2.services.SensorStationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class QRCodeServiceTest {

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private SensorStationService sensorStationService;


    @Test
    void testCreatingQRCode() throws IOException {
        SensorStation sensorStation = sensorStationService.loadSensorStation("71243b9c-1eaf-4d3f-a3c3-2b32a8e96c7e");
        Assertions.assertNotNull(QRCodeService.generateQRCodeImage(sensorStation));
    }
}
