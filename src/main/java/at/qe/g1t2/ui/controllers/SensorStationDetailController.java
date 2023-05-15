package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
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
        accessPointService.saveAccessPoint(sensorStation.getAccessPoint());
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
