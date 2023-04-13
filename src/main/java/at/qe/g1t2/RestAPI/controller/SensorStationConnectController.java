package at.qe.g1t2.RestAPI.controller;

import at.qe.g1t2.RestAPI.exception.EntityNotFoundException;
import at.qe.g1t2.RestAPI.model.LimitsDTO;
import at.qe.g1t2.RestAPI.model.SensorStationDTO;
import at.qe.g1t2.model.AccessPoint;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/sensorStation")
public class SensorStationConnectController {

    @Autowired
    AccessPointService accessPointService;
    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;

    @PostMapping("/register")
    public ResponseEntity<Boolean> createSensorStation(@Valid @RequestBody SensorStationDTO sensorStationDTO) {

        ModelMapper modelMapper = new ModelMapper();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AccessPoint accessPoint = accessPointService.loadAccessPoint(auth.getName());

        SensorStation newSensorStation = modelMapper.map(sensorStationDTO, SensorStation.class);
        SensorStation existingSensorStation = sensorStationService.getSensorStation(sensorStationDTO.getMAC());

        if (existingSensorStation != null) {
            if (!existingSensorStation.getCreateDate().isBefore(LocalDateTime.now().minusMinutes(5))) {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
            }

            sensorStationService.deleteSensorStation(existingSensorStation);

        }
        newSensorStation = sensorStationService.saveSensorStation(accessPoint, newSensorStation);
        for (SensorDataType type : SensorDataType.values()) {
            SensorDataTypeInfo info = new SensorDataTypeInfo();
            info.setMaxLimit(0.0);
            info.setMinLimit(0.0);
            info.setType(type);
            sensorDataTypeInfoService.save(newSensorStation, info);

        }
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

    }

    /*
     for (SensorDataType type : SensorDataType.values()) {
            SensorDataTypeInfo info = new SensorDataTypeInfo();
            info.setMaxLimit(0.0);
            info.setMinLimit(0.0);
            info.setType(type);
            sensorDataTypeInfoService.save(newSensorStation, info);

        }
     */
    @GetMapping("/enabled/{dipId}")
    public ResponseEntity<Boolean> checkEnabled(@PathVariable String dipId) {

        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(getAuthAccessPoint().getId(), Long.parseLong(dipId));
        if (sensorStation == null) {
            throw new EntityNotFoundException("SensorStation was not registered before");
        }
        sensorStation.setLastConnectedDate(LocalDateTime.now());
        sensorStationService.saveSensorStation(getAuthAccessPoint(), sensorStation);
        return new ResponseEntity<>(sensorStation.getEnabled(), HttpStatus.OK);
    }

    @GetMapping("/connected/{dipId}")
    public ResponseEntity<Boolean> checkConnection(@PathVariable String dipId) {

        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(getAuthAccessPoint().getId(), Long.parseLong(dipId));
        if (sensorStation == null) {
            throw new EntityNotFoundException("SensorStation was not registered before");
        }
        sensorStation.setLastConnectedDate(LocalDateTime.now());
        sensorStationService.saveSensorStation(getAuthAccessPoint(), sensorStation);
        return new ResponseEntity<>(sensorStation.getConnected(), HttpStatus.OK);
    }


    @GetMapping("/limits/{dipId}")
    public ResponseEntity<List<LimitsDTO>> checkIfAccessPointIsConnectedAndUpdateLimits(@PathVariable String dipId) {
        String accessPointId = SecurityContextHolder.getContext().getAuthentication().getName();
        AccessPoint accessPoint = accessPointService.loadAccessPoint(accessPointId);
        if (accessPoint == null) {
            throw new EntityNotFoundException("AccessPoint is not registered");
        }
        accessPoint.setLastConnectedDate(LocalDateTime.now());
        accessPointService.saveAccessPoint(accessPoint);
        SensorStation sensorStation = accessPointService.getSensorStationByAccessPointIdAndDipId(accessPointId, Long.parseLong(dipId));
        if (sensorStation == null) {
            throw new EntityNotFoundException("SensorStation does not exist in database. You have to first connect the Sensor Station");

        }
        sensorStation.setLastConnectedDate(LocalDateTime.now());
        sensorStationService.saveSensorStation(accessPoint, sensorStation);
        List<LimitsDTO> limitsDTOS = new ArrayList<>();
        sensorDataTypeInfoService.getAllSensorDataTypeInfosBySensorStation(sensorStation).forEach(x -> {
            if (x != null) {
                LimitsDTO limitsDTO = new LimitsDTO();
                limitsDTO.setMaxLimit(x.getMaxLimit());
                limitsDTO.setMinLimit(x.getMinLimit());
                limitsDTO.setDataType(x.getType().name());
                limitsDTOS.add(limitsDTO);
            }
        });

        return new ResponseEntity<>(limitsDTOS, HttpStatus.OK);


    }

    public AccessPoint getAuthAccessPoint() {
        String accessPointId = SecurityContextHolder.getContext().getAuthentication().getName();
        AccessPoint accessPoint = accessPointService.loadAccessPoint(accessPointId);
        if (accessPoint == null) {
            throw new EntityNotFoundException("AccessPoint is not registered");
        }
        return accessPoint;
    }
}