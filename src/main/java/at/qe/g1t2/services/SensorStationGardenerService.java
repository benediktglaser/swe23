package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.SensorStationGardener;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.SensorStationGardenerRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@Scope("application")
public class SensorStationGardenerService {


    @Autowired
    SensorStationGardenerRepository sensorStationGardenerRepository;
    @Autowired
    UserxRepository userxRepository;
    @Autowired
    SensorStationRepository sensorStationRepository;




    public SensorStationGardener loadSensorStationGardener(UUID uuid){
        return sensorStationGardenerRepository.findSensorStationGardenerById(uuid);
    }


    public SensorStationGardener save(Userx gardener, SensorStation sensorStation, SensorStationGardener sensorStationGardener){
        gardener.getSensorStationGardener().add(sensorStationGardener);
        sensorStation.getSensorStationGardener().add(sensorStationGardener);
        sensorStationGardener.setSensorStation(sensorStation);
        sensorStationGardener.setGardener(gardener);
        SensorStationGardener sensorStationGardener1 = sensorStationGardenerRepository.save(sensorStationGardener);
        System.out.println(sensorStationGardener1.getId());
        return sensorStationGardener1;
    }

    public Collection<SensorStation> getAllSensorStationsOfUser(){
        return sensorStationGardenerRepository.getSensorStationsByGardener(getAuthenticatedUser());
    }

    private Userx getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userxRepository.findFirstByUsername(auth.getName());
    }
}
