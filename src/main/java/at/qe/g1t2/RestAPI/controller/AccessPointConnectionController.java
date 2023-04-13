package at.qe.g1t2.RestAPI.controller;

import at.qe.g1t2.RestAPI.exception.EntityNotFoundException;
import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
// 💡 This is a Java class that serves as a REST controller for handling HTTP requests related to access points. It is annotated with `@RestController` and `@RequestMapping('/api/accessPoint/')` to indicate that it handles requests for the specified URL path.
//
//The class has three instance variables that are autowired with services: `accessPointService`, `sensorDataTypeInfoService`, and `sensorStationService`.
//
//There are two HTTP GET methods defined in the class: `checkIfAccessPointIsConnectedAndSendInterval()` and `switchToCoupleMode()`. Both methods retrieve the authenticated access point, update its last connected date, and save it using the `accessPointService`.
//
//The `checkIfAccessPointIsConnectedAndSendInterval()` method returns a `ResponseEntity` containing the sending interval of the access point as a `Double`. The `switchToCoupleMode()` method returns a `ResponseEntity` containing the couple mode of the access point as a `Boolean`.
//
//Both methods return an HTTP status code of 200 (OK) if the request is successful.null

@RestController
@RequestMapping("/api/accessPoint/")
public class AccessPointConnectionController {
    @Autowired
    AccessPointService accessPointService;
    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;

    @Autowired
    SensorStationService sensorStationService;


    @GetMapping("/interval")
    public ResponseEntity<Double> checkIfAccessPointIsConnectedAndSendInterval() {
        AccessPoint accessPoint = refreshConnection();


        return new ResponseEntity<>(accessPoint.getSendingInterval(), HttpStatus.OK);

    }

    @GetMapping("/couple")
    public ResponseEntity<Boolean> switchToCoupleMode() {

        AccessPoint accessPoint = refreshConnection();
        System.out.println(accessPoint.getAccessPointID());
        return new ResponseEntity<>(accessPoint.getCoupleMode(), HttpStatus.OK);
    }

    @GetMapping("/enabled")
    public ResponseEntity<Boolean> checkEnabled() {
        AccessPoint accessPoint = refreshConnection();
        return new ResponseEntity<>(accessPoint.getEnabled(), HttpStatus.OK);
    }



    public AccessPoint getAuthAccessPoint(){
        String accessPointId = SecurityContextHolder.getContext().getAuthentication().getName();
        AccessPoint accessPoint = accessPointService.loadAccessPoint(accessPointId);
        if(accessPoint == null){
            throw new EntityNotFoundException("AccessPoint is not registered");
        }
        return accessPoint;
    }

    private AccessPoint refreshConnection(){
        AccessPoint accessPoint = getAuthAccessPoint();
        accessPoint.setLastConnectedDate(LocalDateTime.now());
        return accessPointService.saveAccessPoint(accessPoint);
    }
}
