package at.qe.g1t2.ui.controllers;


import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.SensorStationGardener;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationGardenerService;
import at.qe.g1t2.services.SensorStationService;

import at.qe.g1t2.services.UserService;
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
    SensorStationGardenerService sensorStationGardenerService;
    @Autowired
    SessionSensorStationBean sessionSensorStationBean;

    @Autowired
    UserService userService;


    public Collection<SensorStation> getAllOwnSensorStations(){

        return sensorStationGardenerService.getAllSensorStationsOfUser();
    }

    public Collection<SensorStation> getAllSensorStations(){

        return sensorStationService.getAllSensorStations();
    }



    public String redirectToSensorDataPage(SensorStation sensorStation) {
        sessionSensorStationBean.setSensorStation(sensorStation);
        return "sensorData.xhtml?faces-redirect=true";
    }
}
