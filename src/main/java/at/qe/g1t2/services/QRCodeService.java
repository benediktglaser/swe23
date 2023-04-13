package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorStation;
import net.glxn.qrgen.javase.QRCode;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class QRCodeService {
    public static BufferedImage generateQRCodeImage(SensorStation sensorStation) throws Exception {
        ByteArrayOutputStream stream = QRCode
                .from("localhost:8080/visitor/gallery.xhtml?id="+sensorStation.getId())
                .withSize(250, 250)
                .stream();
        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

        return ImageIO.read(bis);
    }


}
