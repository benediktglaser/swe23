package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@Controller
@Scope("view")
public class AccessPointCoupleController {

    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    AccessPointService accessPointService;

    private Boolean isCoupling = false;

    private AccessPoint accessPoint;
    public void startCouplingMode(AccessPoint accessPoint) {
        System.out.println(accessPoint);
        accessPoint.setLastCouplingDate(LocalDateTime.now());
        accessPoint.setCoupleMode(true);
        this.accessPoint = accessPointService.saveAccessPoint(accessPoint);
        System.out.println(accessPoint.getCoupleMode());


    }
    public void endCouplingMode(AccessPoint accessPoint) {
        System.out.println(accessPoint);
        accessPoint.setLastCouplingDate(accessPoint.getLastCouplingDate().plusMinutes(5));
        this.accessPoint = accessPointService.saveAccessPoint(accessPoint);


    }

    public Boolean getCoupling() {
        if(accessPoint!=null){
            System.out.println(accessPointService.loadAccessPoint(accessPoint.getId()).getCoupleMode()+"Getter");
        }
        System.out.println(accessPoint+"Getter");
        return this.accessPoint != null && accessPointService.loadAccessPoint(accessPoint.getId()).getCoupleMode();
    }
}
