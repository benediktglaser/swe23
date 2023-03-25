package at.qe.g1t2.services;


import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Component
@Scope("application")
/**
 * This Class saves/delete Sensorstations and allows tho remove/swap the gardener of the stations
 */
public class SensorStationService {
    @Autowired
    SensorStationRepository sensorStationRepository;

    @Autowired
    UserxRepository userRepository;


    public SensorStation loadSensorStation(UUID uuid){

        return sensorStationRepository.findSensorStationById(uuid);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public SensorStation saveSensorStation(SensorStation sensorStation){

        if(sensorStation.isNew()){

            LocalDateTime createDate = LocalDateTime.now();
            sensorStation.setCreateDate(createDate);

        }
        return sensorStationRepository.save(sensorStation);

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteSensorStation(SensorStation sensorStation){
        sensorStationRepository.delete(sensorStation);
    }



    public Collection<SensorStation> getAllSensorStations(){
        return sensorStationRepository.findAll();

    }




}
