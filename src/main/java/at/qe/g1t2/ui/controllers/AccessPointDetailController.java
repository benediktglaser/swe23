package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Controller for managing the accesspoint-entity.
 * Methods for saving, deleting and reloading.
 */
@Controller
@Scope("view")
public class AccessPointDetailController {
    @Autowired
    private AccessPointService accessPointService;

    private AccessPoint accessPoint;

    public AccessPoint getAccessPoint() {
        return accessPoint;
    }

    public void setAccessPoint(AccessPoint accessPoint) {
        this.accessPoint = accessPoint;
    }

    public void doDeleteAccessPoint() {
        accessPointService.deleteAccessPoint(accessPoint);
        accessPoint = null;
    }

    public void doSaveAccessPoint() {
        accessPointService.saveAccessPoint(accessPoint);
    }

    public void doReloadAccessPoint() {
        accessPoint = this.accessPointService.loadAccessPoint(accessPoint.getId());
    }

    public void doSaveAccessPoint(AccessPoint accessPoint) {
        accessPointService.saveAccessPoint(accessPoint);
    }

}
