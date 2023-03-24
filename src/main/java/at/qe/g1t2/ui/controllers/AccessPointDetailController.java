package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("view")
public class AccessPointDetailController {
    @Autowired
    AccessPointService accessPointService;

    private AccessPoint accessPoint;

    public AccessPoint getAccessPoint() {
        return accessPoint;
    }

    public void setAccessPoint(AccessPoint accessPoint) {
        this.accessPoint = accessPoint;
    }

    public void doDeleteAccessPoint(){
        accessPointService.deleteAccessPoint(accessPoint);
        accessPoint=null;
    }

    public void doSaveAccessPoint(){
        accessPointService.saveAccessPoint(accessPoint);
    }

    public void doReloadAccessPoint(){
        accessPoint = this.accessPointService.loadAccessPoint(accessPoint.getId());
    }
}
