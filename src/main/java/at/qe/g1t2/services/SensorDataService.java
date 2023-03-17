package at.qe.g1t2.services;


import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.SensorDataRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
@Scope("application")
public class SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;


    @PreAuthorize("hasAuthority('ADMIN')")
    public Collection<SensorData> getAllSensorDataByStation(String uuid) {
        return sensorDataRepository.findBySensorStationId(UUID.fromString(uuid));
    }


}