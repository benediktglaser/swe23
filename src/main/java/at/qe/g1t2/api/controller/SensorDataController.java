package at.qe.g1t2.api.controller;

import at.qe.g1t2.api.model.SensorDataTransfer;
import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/sensordata")
public class SensorDataController {

    @Autowired
    SensorDataService sensorDataService;


    @Autowired
    SensorStationService sensorStationService;


    @PostMapping()
    private ResponseEntity<SensorData> createMeasurement(@RequestBody SensorDataTransfer data) {
        ModelMapper modelMapper = new ModelMapper();
        SensorData sensorData = modelMapper.map(data, SensorData.class);
        if (data.getSensorStation() != null) {
            SensorStation station = sensorStationService.loadSensorStation(data.getSensorStation());
            sensorDataService.saveSensorData(station, sensorData);
        }
        return new ResponseEntity<>(sensorData, HttpStatus.ACCEPTED);
    }

    /*
    @GetMapping("/api/sensorData/{id}")
    private SensorData getOneMeasurement(@PathVariable String id) {
        return sensorDataService.loadSensorData(UUID.fromString(id));
    }

    //Do we need to update the

    @PatchMapping("/api/measurements/{id}")
    private SensorDataTransfer updateMeasurement(@PathVariable String id, @RequestBody SensorDataTransfer measurement) {
        return measurementService.updateMeasurement(id, measurement);
    }
     */

}