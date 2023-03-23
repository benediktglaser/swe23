package at.qe.g1t2.services;


import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorDataRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * Service for accessing and sensor data from the access point
 */
@Component
@Scope("application")
public class SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private SensorStationRepository sensorStationRepository;

    @Autowired
    private SensorStationService sensorStationService;


    @PreAuthorize("hasAuthority('ADMIN')")
    public SensorData loadSensorData(UUID uuid) {
        return sensorDataRepository.findSensorDataById(uuid);
    }


    public SensorData saveSensorData(SensorStation sensorStation, SensorData sensorData) {
        if (sensorData.isNew()) {
            LocalDateTime createDate = LocalDateTime.now();
            sensorData.setCreateDate(createDate);
            sensorData.setSensorStation(sensorStation);
            sensorStation.getSensorData().add(sensorData);
            sensorStationRepository.save(sensorStation);
            return sensorStationService.loadSensorStation(sensorStation.getId()).getSensorData().get(sensorStation.getSensorData().size() - 1);
        }
        sensorDataRepository.save(sensorData);
        return sensorData;
    }



    public Collection<SensorData> getAllSensorDataByStation(String uuid) {
        return sensorDataRepository.findBySensorStationId(UUID.fromString(uuid));
    }

    public Collection<SensorData> getAllSensorDataByType(String type) {
        return sensorDataRepository.findByType(type);
    }


}