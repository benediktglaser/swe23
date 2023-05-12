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

/**
 * This controller is responsible for starting and ending the couple mode
 */
@Controller
@Scope("view")
public class AccessPointCoupleController {

    @Autowired
    private SensorStationService sensorStationService;

    @Autowired
    private CoupleBean coupleBean;

    @Autowired
    private AccessPointService accessPointService;

    private AccessPoint accessPoint;


    public void startCouplingMode(AccessPoint accessPoint) {
        System.out.println(accessPoint + "startCouple");
        accessPoint.setCoupleMode(true);
        this.accessPoint = accessPointService.saveAccessPoint(accessPoint);
        coupleBean.addAccessPoint(accessPoint);
    }

    public void endCouplingMode(AccessPoint accessPoint) {
        accessPoint.setCoupleMode(false);
        this.accessPoint = accessPointService.saveAccessPoint(accessPoint);
    }

    /**
     * If the accesspoint is successfully coupled with a sensorstation, this
     * method will be called to display an according message and hide the sensorstation,
     * because it is already taken.
     * @param dipId
     */
    public void connectHandler(Long dipId) {
        if(accessPoint == null){
            return;
        }
        System.out.println(accessPoint + "Handler");
        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(accessPoint.getAccessPointID(), dipId);
        if (sensorStation != null && sensorStation.getConnected()) {
            PrimeFaces.current().executeScript("PF('statusDialog').hide()");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "SensorStation with DipId " + sensorStation.getDipId() + " connected successfully!", null));
        }
    }


}
