package at.qe.g1t2.RestAPI.controller;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorDataRepository;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/chart")
public class ChartController {


    @Autowired
    SensorDataService sensorDataService;

    @Autowired
    SensorStationService sensorStationService;

    @GetMapping()
    public String getSensorDataCSV(@RequestParam String id) throws JsonProcessingException {
        System.out.println(id);

        List<Object[]> dates = sensorDataService.getAllSensorDataByStationForChart(sensorStationService.loadSensorStation(id));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());


        String json = objectMapper.writeValueAsString(dates);
        System.out.println(json);
        return json;
    }
}
