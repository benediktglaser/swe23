package at.qe.g1t2.ui.controllers;


import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationService;

import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;


@Component
@Scope("view")
public class SensorStationListController {
    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    SensorDataService sensorDataService;

    @Autowired
    SessionSensorStationBean sessionSensorStationBean;


    public Collection<SensorStation> getAllOwnSensorStations(){

        return sensorStationService.getAllOwnSensorStations();
    }

    public Collection<SensorStation> getAllSensorStations(){

        return sensorStationService.getAllSensorStations();
    }



    public String redirectToSensorDataPage(SensorStation sensorStation) {
        sessionSensorStationBean.setSensorStation(sensorStation);
        return "sensorData.xhtml?faces-redirect=true";
    }
}
