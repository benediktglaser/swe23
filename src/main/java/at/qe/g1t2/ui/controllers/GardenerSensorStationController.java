package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.SensorStationGardenerService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.UserService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("session")
public class GardenerSensorStationController {
    @Autowired
    UserService userService;

    @Autowired
    SessionSensorStationBean sessionSensorStationBean;

    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    SensorStationGardenerService sensorStationGardenerService;

    private String username;

    public void assignSensorStationToGardener(SensorStation sensorStation){
        Userx userToBeAssigned = userService.loadUser(username);
        if(userToBeAssigned!=null) {
            sensorStationGardenerService.assignGardener(userToBeAssigned, sensorStation);
        }
        username="";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
