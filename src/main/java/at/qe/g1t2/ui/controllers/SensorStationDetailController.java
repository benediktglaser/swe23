package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("view")
public class SensorStationDetailController {

    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    SessionSensorStationBean sessionSensorStationBean;

    @Autowired
    SensorDataTypeInfoController sensorDataTypeInfoController;

    private SensorStation sensorStation;

    public void saveSensorStation(SensorStation sensorStation){
        sensorStationService.saveSensorStation(sensorStation.getAccessPoint(),sensorStation);
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
}
