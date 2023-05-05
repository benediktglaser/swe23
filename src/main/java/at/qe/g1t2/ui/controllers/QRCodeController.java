package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.restapi.exception.InvalidAccessException;
import at.qe.g1t2.services.SensorStationService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.jboss.weld.context.RequestContext;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@Scope("view")
public class QRCodeController {
    @Autowired
    private SensorStationService sensorStationService;
    private SensorStation sensorStation;


    @PostConstruct
    public void myMethod() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        externalContext.redirect("/login.xhtml");
        String myParam = externalContext.getRequestParameterMap().get("id");
        if(myParam == null){
            try {
                System.out.println(externalContext);
                System.out.println(externalContext.getContextName());
                externalContext.redirect("/login.xhtml");
                return;
            } catch (IOException e) {
                throw new InvalidAccessException("Redirecting to non existing Page");
            }

        }
        SensorStation sensorStation = sensorStationService.loadSensorStation(myParam);
        if(sensorStation == null){
            try {
                externalContext.redirect("/login.xhtml");
                return;
            } catch (IOException e) {
                throw new InvalidAccessException("Redirecting to non existing Page");
            }

        }
        this.sensorStation = sensorStation;

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
