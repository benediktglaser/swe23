package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.restapi.exception.EntityNotFoundException;
import at.qe.g1t2.restapi.model.LimitsDTO;
import at.qe.g1t2.restapi.model.SensorStationDTO;
import at.qe.g1t2.restapi.model.SensorStationRegisterDTO;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.VisibleSensorStationsService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensorStation")
public class SensorStationConnectController {


    private Map<AccessPoint,Map<Long,SensorStationDTO>> visibleMap = new HashMap<>();

    @Autowired
    private VisibleSensorStationsService visibleSensorStationsService;
    @Autowired
    private AccessPointService accessPointService;
    @Autowired
    private SensorStationService sensorStationService;

    @Autowired
    private SensorDataTypeInfoService sensorDataTypeInfoService;

    @PostMapping("/register")
    public ResponseEntity<SensorStationRegisterDTO> addVisibleSensorStations(@Valid @RequestBody SensorStationDTO sensorStationDTO) {

        AccessPoint accessPoint = getAuthAccessPoint();

        SensorStation existingSensorStation = sensorStationService.getSensorStation(sensorStationDTO.getMac());
        SensorStationRegisterDTO sensorStationRegisterDTO = new SensorStationRegisterDTO();
        if (existingSensorStation != null && existingSensorStation.getAccessPoint().equals(accessPoint)) {
            sensorStationRegisterDTO.setAvailable(false);
            sensorStationRegisterDTO.setAlreadyConnected(true);
            return new ResponseEntity<>(sensorStationRegisterDTO, HttpStatus.OK);
        }
        if  (existingSensorStation != null){
            sensorStationRegisterDTO.setAvailable(false);
            sensorStationRegisterDTO.setAlreadyConnected(false);
            return new ResponseEntity<>(sensorStationRegisterDTO, HttpStatus.OK);
        }
        sensorStationDTO.setConnected(false);
        sensorStationDTO.setVerified(false);
        sensorStationRegisterDTO.setAvailable(true);
        sensorStationRegisterDTO.setAlreadyConnected(false);
        visibleSensorStationsService.addVisibleStation(accessPoint,sensorStationDTO);
        return new ResponseEntity<>(sensorStationRegisterDTO, HttpStatus.OK);

    }

    @GetMapping("/verified/{dipId}")
    public ResponseEntity<Boolean> checkVerifiedSensorStationsForCouple(@PathVariable String dipId) {
        AccessPoint accessPoint = getAuthAccessPoint();
        SensorStationDTO sensorStationDTO = visibleSensorStationsService.getSensorStationByAccessPointAndDipId(accessPoint,dipId);

        return new ResponseEntity<>(sensorStationDTO.getVerified(),HttpStatus.OK);
    }


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
        AccessPoint accessPoint = getAuthAccessPoint();
        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(getAuthAccessPoint().getId(), Long.parseLong(dipId));
        if (sensorStation == null) {
            SensorStationDTO sensorStationDTO = visibleSensorStationsService.getSensorStationByAccessPointAndDipId(accessPoint,dipId);
            if(sensorStationDTO == null){
                throw new EntityNotFoundException("SensorStation not verified or registered");
            }
            SensorStation newSensorStation = new SensorStation();
            newSensorStation.setDipId(sensorStationDTO.getDipId());
            newSensorStation.setMac(sensorStationDTO.getMac());
            newSensorStation.setLastConnectedDate(LocalDateTime.now());
            newSensorStation = sensorStationService.saveSensorStation(accessPoint, newSensorStation);
            sensorStationDTO.setConnected(true);
            visibleSensorStationsService.replaceSensorStationDTO(accessPoint,dipId,sensorStationDTO);
            return new ResponseEntity<>(newSensorStation.getConnected(), HttpStatus.OK);
        }
        sensorStation.setLastConnectedDate(LocalDateTime.now());
        sensorStationService.saveSensorStation(accessPoint, sensorStation);
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