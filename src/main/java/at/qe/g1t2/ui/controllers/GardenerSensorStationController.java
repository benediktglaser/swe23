package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.UserRole;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.SensorStationGardenerService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.UserService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Controller for assigning gardeners to a sensorstation.
 * This class is only used by administrators.
 */

@Controller
@Scope("view")
public class GardenerSensorStationController {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionSensorStationBean sessionSensorStationBean;

    @Autowired
    private SensorStationService sensorStationService;

    @Autowired
    private SensorStationGardenerService sensorStationGardenerService;


    private SensorStation sensorStation;

    private String username;

    private boolean isValid;

    /**
     * This method assign a gardener to a given sensorstation.
     * In case of a wrong username an error-message is displayed.
     *
     * @param sensorStation
     */
    public void assignSensorStationToGardener(SensorStation sensorStation) {
        Userx userToBeAssigned = userService.loadUser(username);
        if (userToBeAssigned != null && userToBeAssigned.getRoles().contains(UserRole.GARDENER)) {
            sensorStationGardenerService.assignGardener(userToBeAssigned, sensorStation);
            isValid = true;
            PrimeFaces.current().executeScript("isValid('true')");

        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username does not exists", null));
            isValid = false;
            PrimeFaces.current().executeScript("isValid('false')");

        }
    }

    /**
     * This method displays an notification, that a gardener
     * has arrived at the given sensorstation
     *
     * @param sensorStation
     */
    public void gardenerIsHere(SensorStation sensorStation) {
        boolean isThere = sensorStationGardenerService.getGardenerIsHere().contains(sensorStation);
        if (isThere) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Gardener is at the station", null));
            PrimeFaces.current().ajax().update("msgs");
            sensorStationGardenerService.getGardenerIsHere().remove(sensorStation);
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }

}
