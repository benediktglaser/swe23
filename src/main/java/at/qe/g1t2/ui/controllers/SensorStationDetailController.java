package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("view")
public class SensorStationDetailController {

    @Autowired
    SensorStationService sensorStationService;



    public void saveSensorStation(SensorStation sensorStation){
        sensorStationService.saveSensorStation(sensorStation.getAccessPoint(),sensorStation);
    }
}
