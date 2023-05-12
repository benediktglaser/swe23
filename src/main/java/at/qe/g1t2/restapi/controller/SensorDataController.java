package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.restapi.model.SensorDataDTO;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class provides a REST-interface for the accessPoints to send data from the sensorstations to the webserver
 */
@RestController
@RequestMapping("/api/sensorData")
public class SensorDataController {

    @Autowired
    SensorDataService sensorDataService;


    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    SensorStationRepository sensorStationRepository;

    /**
     * This method receives data measurements from the accessPoints.
     * It returns True if the data has successfully been saved.
     * If the sensorstation does not exist, False and Http.Status.BAD_REQUEST will be returned.
     *
     * @param SensorDataDTO data
     * @return ResponseEntity<Boolean>
     */
    @PostMapping
    public ResponseEntity<Boolean> createMeasurement(@Valid @RequestBody SensorDataDTO data) {
        ModelMapper modelMapper = new ModelMapper();
        SensorData sensorData = modelMapper.map(data, SensorData.class);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String accessPointId = auth.getName();
        Long dipId = data.getDipId();
        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(accessPointId, dipId);
        if (sensorStation == null) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
        sensorDataService.saveSensorData(sensorStation, sensorData);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED);
    }


}