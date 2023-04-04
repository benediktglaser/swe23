package at.qe.g1t2.RestAPI.Controller;

import at.qe.g1t2.RestAPI.model.SensorDataDTO;
import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
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


@RestController
@RequestMapping("/api/sensorData")
public class SensorDataController {

    @Autowired
    SensorDataService sensorDataService;


    @Autowired
    SensorStationService sensorStationService;


    @PostMapping
    private ResponseEntity<Boolean> createMeasurement(@Valid @RequestBody SensorDataDTO data) {
        ModelMapper modelMapper = new ModelMapper();
        SensorData sensorData = modelMapper.map(data, SensorData.class);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String accessPointId = auth.getName();
        System.out.println(sensorData);
        Long dipId = data.getDipId();
        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(accessPointId,dipId);
        sensorDataService.saveSensorData(sensorStation,sensorData);

        return new ResponseEntity<>(Boolean.TRUE,HttpStatus.CREATED);
    }


}