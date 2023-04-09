package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Scope("view")
public class PlantsForUsersController {

    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    UserService userService;

    private SensorStation sensorStation;

    public Collection<SensorStation> getAllSensorStations(){

        return sensorStationService.getAllSensorStations();
    }

    public void setUsersFavourite(SensorStation sensorStation){
        userService.addSensorStationToUser(sensorStation);
    }


}
