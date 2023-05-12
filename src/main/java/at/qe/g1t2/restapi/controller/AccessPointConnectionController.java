package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.restapi.exception.EntityNotFoundException;
import at.qe.g1t2.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * This is a Java class that serves as a REST controller for handling HTTP requests related to access points. It is annotated with `@RestController` and `@RequestMapping('/api/accessPoint/')` to indicate that it handles requests for the specified URL path.
 * The class has three instance variables that are autowired with services: `accessPointService`, `sensorDataTypeInfoService`, and `sensorStationService`.
 * <p>
 * There are two HTTP GET methods defined in the class: `checkIfAccessPointIsConnectedAndSendInterval()` and `switchToCoupleMode()`. Both methods retrieve the authenticated access point, update its last connected date, and save it using the `accessPointService`.
 * <p>
 * The `checkIfAccessPointIsConnectedAndSendInterval()` method returns a `ResponseEntity` containing the sending interval of the access point as a `Double`. The `switchToCoupleMode()` method returns a `ResponseEntity` containing the couple mode of the access point as a `Boolean`.
 * <p>
 * Both methods return an HTTP status code of 200 (OK) if the request is successful.
 **/

@RestController
@RequestMapping("/api/accessPoint/")
public class AccessPointConnectionController {
    @Autowired
    AccessPointService accessPointService;

    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;

    @Autowired
    SensorStationService sensorStationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessPointConnectionController.class);

    /**
     * This method retrieve the authenticated access point and sends the current combined Sending Interval
     *
     * @return combinedInterval(sendingInterval + threshold) and Http.Status.OK
     */
    @GetMapping("/interval")
    public ResponseEntity<Double> checkIfAccessPointIsConnectedAndSendInterval() {
        AccessPoint accessPoint = refreshConnection();
        LogMsg<String, AccessPoint> msg = new LogMsg<>(LogMsg.LogType.OTHER, AccessPoint.class, "Access point: " + accessPoint.getAccessPointID(), "AccessPoint asked for sending interval", "Access point: " + accessPoint.getAccessPointID());
        LOGGER.info(msg.getMessage());
        return new ResponseEntity<>(accessPoint.getCombinedInterval(), HttpStatus.OK);

    }

    /**
     * This method retrieve the authenticated access point and sends if the accesspoint is in the
     * couple mode.
     *
     * @return boolean and Http.Status.OK
     */
    @GetMapping("/couple")
    public ResponseEntity<Boolean> switchToCoupleMode() {

        AccessPoint accessPoint = refreshConnection();
        LOGGER.info(accessPoint.toString());
        String switched = accessPoint.getCoupleMode().toString();
        LogMsg<String, AccessPoint> msg = new LogMsg<>(LogMsg.LogType.OTHER, AccessPoint.class, "Access point: " + accessPoint.getAccessPointID(), "AccessPoint ask for coupling mode / Switched: " + switched, "Access point: " + accessPoint.getAccessPointID());
        LOGGER.info(msg.getMessage());

        return new ResponseEntity<>(accessPoint.getCoupleMode(), HttpStatus.OK);
    }

    /**
     * This method retrieve the authenticated access point and sends as return if the accessPoint is enabled or not
     * If the AccessPoint is not enabled it will receive Http.Status 401 otherwise true.
     *
     * @return boolean and Http.Status.OK
     */
    @GetMapping("/enabled")
    public ResponseEntity<Boolean> checkEnabled() {
        AccessPoint accessPoint = refreshConnection();
        String enabled = accessPoint.getEnabled().toString();
        LogMsg<String, AccessPoint> msg = new LogMsg<>(LogMsg.LogType.OTHER, AccessPoint.class, "Access point: " + accessPoint.getAccessPointID(), "AccessPoint ask for coupling mode / Enabled =" + enabled, "Access point: " + accessPoint.getAccessPointID());
        LOGGER.info(msg.getMessage());
        return new ResponseEntity<>(accessPoint.getEnabled(), HttpStatus.OK);
    }

    /**
     * This method identifies the AccessPoint, who sent the message.
     *
     * @return AccessPoint
     */
    public AccessPoint getAuthAccessPoint() {
        String accessPointId = SecurityContextHolder.getContext().getAuthentication().getName();
        AccessPoint accessPoint = accessPointService.loadAccessPoint(accessPointId);
        if (accessPoint == null) {
            LogMsg<String, AccessPoint> msg = new LogMsg<>(LogMsg.LogType.FAILURE, AccessPoint.class, null, "Entity Not found Exception", null);
            LOGGER.error(msg.getMessage());
            throw new EntityNotFoundException("AccessPoint is not registered");
        }
        LogMsg<String, AccessPoint> msg = new LogMsg<>(LogMsg.LogType.OTHER, AccessPoint.class, "Access point: " + accessPoint.getAccessPointID(), "Call get getAuthAccessPoint()", "Access point: " + accessPoint.getAccessPointID());
        LOGGER.warn(msg.getMessage());
        return accessPoint;
    }

    /**
     * This method sets date, at which the current authenticated AccessPoint was last
     * connected to the current Date
     *
     * @return AccessPoint
     */
    private AccessPoint refreshConnection() {
        AccessPoint accessPoint = getAuthAccessPoint();
        accessPoint.setLastConnectedDate(LocalDateTime.now());
        LogMsg<String, AccessPoint> msg = new LogMsg<>(LogMsg.LogType.CONNECTED, AccessPoint.class, "Access point: " + accessPoint.getAccessPointID(), "AccessPoint is Connected", "Access point: " + accessPoint.getAccessPointID());
        LOGGER.error(msg.getMessage());
        return accessPointService.saveAccessPoint(accessPoint);
    }
}
