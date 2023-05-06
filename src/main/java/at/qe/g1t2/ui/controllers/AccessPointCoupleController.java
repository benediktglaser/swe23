package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.ui.beans.CoupleBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


@Controller
@Scope("view")
public class AccessPointCoupleController {

    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    private CoupleBean coupleBean;

    @Autowired
    AccessPointService accessPointService;

    private AccessPoint accessPoint;

    public void startCouplingMode(AccessPoint accessPoint) {
        accessPoint.setCoupleMode(true);
        this.accessPoint = accessPointService.saveAccessPoint(accessPoint);
        coupleBean.addAccessPoint(accessPoint);
    }

    public void endCouplingMode(AccessPoint accessPoint) {
        accessPoint.setCoupleMode(false);
        this.accessPoint = accessPointService.saveAccessPoint(accessPoint);
    }

    public void connectHandler(Long dipId) {
        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(accessPoint.getAccessPointID(), dipId);
        if (sensorStation != null && sensorStation.getConnected()) {
            PrimeFaces.current().executeScript("PF('statusDialog').hide()");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "SensorStation with DipId " + sensorStation.getDipId() + " connected successfully!", null));
        }
    }

}
