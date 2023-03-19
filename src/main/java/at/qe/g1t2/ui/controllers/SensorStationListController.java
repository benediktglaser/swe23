package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.UserxRepository;
import at.qe.g1t2.services.SensorStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Scope("view")
public class SensorStationListController {
    @Autowired
    SensorStationService sensorStationService;



    public Collection<SensorStation> getAllOwnSensorStations(){

        return sensorStationService.getAllOwnSensorStations();
    }

    public Collection<SensorStation> getAllSensorStations(){

        return sensorStationService.getAllSensorStations();
    }
}
