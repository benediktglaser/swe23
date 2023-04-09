package at.qe.g1t2.RestAPI.controller;

import at.qe.g1t2.RestAPI.model.ChartDTO;
import at.qe.g1t2.model.SensorData;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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

    @PostMapping()
    public String getSensorDataCSV(@RequestBody ChartDTO chartDTO) throws JsonProcessingException {
        System.out.println(chartDTO.getSensorDataTypeInfoId());
        SensorStation sensorStation = sensorStationService.loadSensorStation(chartDTO.getSensorStationId());

        System.out.println(chartDTO.getSensorDataTypeInfoId());
        SensorDataTypeInfo sensorDataTypeInfo = sensorDataTypeInfoService.loadSensorDataTypeInfo(chartDTO.getSensorDataTypeInfoId());
        List<Object[]> dataset = new ArrayList<>();
        dataset = sensorDataService.getAllSensorDataByStationAndTypeForChart(sensorStation, sensorDataTypeInfo.getType());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ObjectWriter writer = objectMapper.writer().with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ArrayNode seriesArray = objectMapper.createArrayNode();
        ObjectNode seriesObject = objectMapper.createObjectNode();
        seriesObject.put("data", objectMapper.writeValueAsString(dataset));

        seriesObject.put("min", sensorDataTypeInfo.getMinLimit());
        seriesObject.put("max", sensorDataTypeInfo.getMaxLimit());
        seriesArray.add(seriesObject);
        ObjectNode chartObject = objectMapper.createObjectNode();
        chartObject.set("series", seriesArray);
        String json = writer.writeValueAsString(seriesArray);
        System.out.println(json);
        return json;
    }

}
