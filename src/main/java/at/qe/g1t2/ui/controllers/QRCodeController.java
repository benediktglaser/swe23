package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorStationService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("view")
public class QRCodeController {
    @Autowired
    SensorStationService sensorStationService;

    SensorStation sensorStation;

    @PostConstruct
    public void myMethod() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        String myParam = externalContext.getRequestParameterMap().get("id");
        sensorStation = sensorStationService.loadSensorStation(myParam);
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }
    public void doUpdate(String sensorStation){

        PrimeFaces.current().executeScript("sent('"+sensorStation+"')");
    }
}
