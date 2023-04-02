package at.qe.g1t2.RestAPI.Controller;

import at.qe.g1t2.RestAPI.model.SensorDataTransfer;
import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private HttpStatus createMeasurement(@RequestBody SensorDataTransfer data) {
        ModelMapper modelMapper = new ModelMapper();
        SensorData sensorData = modelMapper.map(data, SensorData.class);

        SensorStation station = sensorStationService.loadSensorStation(data.getSensorStation());
        sensorData = sensorDataService.saveSensorData(station, sensorData);
        data.setId(sensorData.getId().toString());

        return HttpStatus.OK;
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