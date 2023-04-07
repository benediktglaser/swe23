package at.qe.g1t2.RestAPI.controller;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.repositories.SensorDataRepository;
import at.qe.g1t2.services.SensorDataService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/chart")
public class ChartController {

    @Autowired
    SensorDataRepository sensorDataRepository;

    @GetMapping()
    public String getSensorDataCSV() throws JsonProcessingException {

        // Get your sensor data and convert it to a CSV string
        List<Object[]> dates = sensorDataRepository.gett();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String json = objectMapper.writeValueAsString(dates);
        System.out.println(json);
        return json;
    }
}
