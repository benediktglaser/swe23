package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.restapi.model.SensorStationDTO;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.VisibleSensorStationsService;
import at.qe.g1t2.ui.beans.CoupleBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

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
    private VisibleSensorStationController visibleSensorStationController;

    @Autowired
    private VisibleSensorStationsService visibleSensorStationsService;
    @Autowired
    private AccessPointService accessPointService;

    private AccessPoint accessPoint;

    private LocalDateTime connectStartTime;

    public LocalDateTime getConnectStartTime() {
        return connectStartTime;
    }

    public void setConnectStartTime(LocalDateTime connectStartTime) {
        this.connectStartTime = connectStartTime;
    }


    public void startCouplingMode(AccessPoint accessPoint) {

        accessPoint = accessPointService.loadAccessPoint(accessPoint.getId());
        if(accessPoint.getCoupleMode()){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Another admin has already started the couple mode. This can take up to 5 minutes!", null));
            return;
        }

        visibleSensorStationController.resetVisibleList(accessPoint);
        accessPoint.setCoupleMode(true);
        this.accessPoint = accessPointService.saveAccessPoint(accessPoint);
        PrimeFaces.current().executeScript("startCoupleMode()");

    }

    public void endCouplingMode(AccessPoint accessPoint) {
        accessPoint.setCoupleMode(false);
        this.accessPoint = accessPointService.saveAccessPoint(accessPoint);
    }
    public void isCoupling(AccessPoint accessPoint) {

        if(accessPoint == null){
            return;
        }
       boolean isTimeOut = !accessPointService.loadAccessPoint(accessPoint.getId()).getCoupleMode();

       if(isTimeOut){
           PrimeFaces.current().executeScript("deactivateCoupleMode()");
       }

    }
    public boolean startPoll(AccessPoint accessPoint) {
        return accessPointService.loadAccessPoint(accessPoint.getId()).getCoupleMode();

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
        SensorStationDTO isVerified = visibleSensorStationsService.getSensorStationByAccessPointAndDipId(accessPoint, String.valueOf(dipId));
        if(isVerified != null && !isVerified.getVerified() && isVerified.isConnectingTimeOut()){
            visibleSensorStationsService.removeSensorStationDTO(accessPoint,String.valueOf(isVerified.getDipId()));
            PrimeFaces.current().executeScript("connectTimeOut()");
        }

        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(accessPoint.getAccessPointID(), dipId);
        if (sensorStation != null && sensorStation.getConnected()) {
            PrimeFaces.current().executeScript("PF('statusDialog').hide()");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "SensorStation with DipId " + sensorStation.getDipId() + " connected successfully!", null));
        }

    }


}
