package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.restapi.exception.QRException;
import at.qe.g1t2.services.QRCodeService;
import at.qe.g1t2.services.SensorStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("")
public class QRCodeRestController {
    @Autowired
    private SensorStationService sensorStationService;

    @GetMapping(value = "/visitor/sensorStations/gallery.xhtml{sensorStationId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> sensorStationsQRCode(@PathVariable String sensorStationId){
        try {
            BufferedImage qrCodeImage = QRCodeService.generateQRCodeImage(sensorStationService.loadSensorStation(sensorStationId));
            ByteArrayOutputStream qr = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImage, "png", qr);
            byte[] qrCodeBytes = qr.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(sensorStationService.loadSensorStation(sensorStationId).getName()+".png").build());

            return ResponseEntity.ok().headers(headers).body(qrCodeBytes);
        }
        catch (IOException io){
            throw new QRException("QR Code generation failed");
        }
    }

    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }


}

