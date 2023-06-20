package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * This controller manages SensorStations which is needed in accessPoint.xhtml. Therefore, contains save function which
 * calls a service function responsible for saving the entity.
 */

@Controller
@Scope("view")
public class SensorStationDetailController {

    @Autowired
    private SensorStationService sensorStationService;

    @Autowired
    SessionSensorStationBean sessionSensorStationBean;

    @Autowired
    AccessPointService accessPointService;

    @Autowired
    SensorDataTypeInfoController sensorDataTypeInfoController;

    private SensorStation sensorStation;

    public void saveSensorStation(SensorStation sensorStation){
        if(sensorStation.getAccessPoint().getSendingInterval()>36000){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Sending interval should be below 36000 seconds.(equals 10 hours)", null));
        }else {
            accessPointService.saveAccessPoint(sensorStation.getAccessPoint());
            sensorStationService.saveSensorStation(sensorStation.getAccessPoint(), sensorStation);
        }
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }

    public String redirectToMinMaxEdit(SensorStation sensorStation) {
        sessionSensorStationBean.setSensorStation(sensorStation);
        return "minMaxEdit.xhtml?faces-redirect=true";
    }

    public void deleteSensorStation(SensorStation sensorStation){
        sensorStationService.deleteSensorStation(sensorStation);
    }
}
