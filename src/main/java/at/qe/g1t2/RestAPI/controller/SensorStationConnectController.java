package at.qe.g1t2.RestAPI.controller;

import at.qe.g1t2.RestAPI.model.SensorStationDTO;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensorStation")
public class SensorStationConnectController {

    @Autowired
    AccessPointService accessPointService;
    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;

    @PostMapping("/connect")
    public ResponseEntity<SensorStationDTO> createSensorStation(@Valid @RequestBody SensorStationDTO sensorStationDTO){

        ModelMapper modelMapper = new ModelMapper();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String accessPointId = auth.getName();

        SensorStation newSensorStation = modelMapper.map(sensorStationDTO,SensorStation.class);
        System.out.println(newSensorStation);
        newSensorStation = sensorStationService.saveSensorStation(accessPointService.loadAccessPoint(accessPointId),newSensorStation);
        //Set Min and Max Value a default value
        for(SensorDataType type: SensorDataType.values()){
            SensorDataTypeInfo info = new SensorDataTypeInfo();
            info.setMaxLimit(0.0);
            info.setMinLimit(0.0);
            info.setType(type);
            sensorDataTypeInfoService.save(newSensorStation,info);

        }
        return new ResponseEntity<>(sensorStationDTO, HttpStatus.OK);
    }
}