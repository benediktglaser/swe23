package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorStationService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;

/**
 * This controller is responsible for handling the QR-code creation.
 */
@Controller
@Scope("view")
public class QRCodeController {
    @Autowired
    private SensorStationService sensorStationService;

    private SensorStation sensorStation;

    @PostConstruct
    public void handlingQRCodes() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            String myParam = externalContext.getRequestParameterMap().get("id");
            if (myParam == null) {
                externalContext.redirect("/login.xhtml");
                return;
            }
            SensorStation sensorStation = sensorStationService.loadSensorStation(myParam);
            if (sensorStation == null) {
                externalContext.redirect("/login.xhtml");
                return;
            }
            this.sensorStation = sensorStation;
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while processing the request.");
        }
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }
}
