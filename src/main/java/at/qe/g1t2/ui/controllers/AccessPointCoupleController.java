package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@Scope("view")
public class AccessPointCoupleController {

    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    AccessPointService accessPointService;

    private AccessPoint accessPoint;

    public void startCouplingMode(AccessPoint accessPoint) {
        accessPoint.setLastCouplingDate(LocalDateTime.now());
        accessPoint.setCoupleMode(true);
        this.accessPoint = accessPointService.saveAccessPoint(accessPoint);


    }

    public void endCouplingMode(AccessPoint accessPoint) {
        accessPoint.setCoupleMode(false);
        this.accessPoint = accessPointService.saveAccessPoint(accessPoint);

    }


}
