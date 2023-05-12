package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorStation;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * This service generates a QR-code for a given sensorstation.
 */
@Service
public class QRCodeService {
    private QRCodeService() {
    }

    public static BufferedImage generateQRCodeImage(SensorStation sensorStation) throws IOException {
        ByteArrayOutputStream stream = QRCode
                .from(getIPAddress() + ":8080/visitor/gallery.xhtml?id=" + sensorStation.getId())
                .withSize(250, 250)
                .stream();
        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

        return ImageIO.read(bis);
    }

    private static String getIPAddress() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("google.com", 80));
            return socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
        }
        return null;
    }
}