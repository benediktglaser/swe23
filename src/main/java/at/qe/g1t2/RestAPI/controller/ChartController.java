package at.qe.g1t2.RestAPI.controller;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorDataRepository;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chart")
public class ChartController {


    @Autowired
    SensorDataService sensorDataService;

    @Autowired
    SensorStationService sensorStationService;
    @Autowired
    SensorDataRepository sensorDataRepository;

    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;

    @GetMapping()
    public String getSensorData(@RequestParam String sensorStationId,@RequestParam String sensorDataTypeId,@RequestParam String typeId) throws JsonProcessingException {

        SensorStation sensorStation = sensorStationService.loadSensorStation(sensorStationId);


        SensorDataTypeInfo sensorDataTypeInfo = sensorDataTypeInfoService.loadSensorDataTypeInfo(sensorDataTypeId);
        List<Object[]> dataset = new ArrayList<>();
        dataset = sensorDataService.getAllSensorDataByStationAndTypeForChart(sensorStation, SensorDataType.valueOf(typeId));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ObjectWriter writer = objectMapper.writer().with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ArrayNode seriesArray = objectMapper.createArrayNode();
        ObjectNode seriesObject = objectMapper.createObjectNode();
        seriesObject.put("data", objectMapper.writeValueAsString(dataset));
        if(sensorDataTypeInfo != null){
            seriesObject.put("min", sensorDataTypeInfo.getMinLimit());
            seriesObject.put("max", sensorDataTypeInfo.getMaxLimit());
        }
        seriesArray.add(seriesObject);
        ObjectNode chartObject = objectMapper.createObjectNode();
        chartObject.set("series", seriesArray);
        String json = writer.writeValueAsString(seriesArray);
        System.out.println(json);
        return json;
    }

    @GetMapping("/add")
    public String getNewSensorData(@RequestParam String sensorStationId,@RequestParam String sensorDataTypeId,@RequestParam String typeId, @RequestParam String lastDate) throws JsonProcessingException {
        long timestamp = Long.parseLong(lastDate);
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime lastTimeStamp = LocalDateTime.of(2020,1,1,1,0,0);
        SensorStation sensorStation = sensorStationService.loadSensorStation(sensorStationId);

        System.out.println(sensorStation);
        System.out.println(lastTimeStamp);
        System.out.println(typeId);
        List<Object[]> dataset = new ArrayList<>();
        dataset = sensorDataService.getAllNewSensorDataByStationAndTypeForChart(sensorStation, SensorDataType.valueOf(typeId),lastTimeStamp);
        System.out.println(dataset.isEmpty());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ObjectWriter writer = objectMapper.writer().with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String json = writer.writeValueAsString(dataset);
        System.out.println(json);
        return json;
    }

}
