package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Controller;

/**
 * This controller manages the download of the QR-Code.
 */
@Controller
public class QRCodeDownloadController {
    private SensorStation sensorStation;

    public void doUpdate(String sensorStation) {
        PrimeFaces.current().executeScript("sent('" + sensorStation + "')");
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }
}
