package at.qe.g1t2.RestAPI.controller;

import at.qe.g1t2.RestAPI.exception.EntityNotFoundException;
import at.qe.g1t2.RestAPI.model.LimitsDTO;
import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        AccessPoint accessPoint = getAuthAccessPoint();
        accessPoint.setLastConnectedDate(LocalDateTime.now());
        accessPointService.saveAccessPoint(accessPoint);


        return new ResponseEntity<>(accessPoint.getSendingInterval(), HttpStatus.OK);

    }

    @GetMapping("/couple")
    public ResponseEntity<Boolean> switchToCoupleMode() {
        AccessPoint accessPoint = getAuthAccessPoint();
        accessPoint.setLastConnectedDate(LocalDateTime.now());
        accessPointService.saveAccessPoint(accessPoint);

        return new ResponseEntity<>(accessPoint.getCoupleMode(), HttpStatus.OK);
    }



    public AccessPoint getAuthAccessPoint(){
        String accessPointId = SecurityContextHolder.getContext().getAuthentication().getName();
        AccessPoint accessPoint = accessPointService.loadAccessPoint(accessPointId);
        if(accessPoint == null){
            throw new EntityNotFoundException("AccessPoint is not registered");
        }
        return accessPoint;
    }
}
