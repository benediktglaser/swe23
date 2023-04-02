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


import java.util.Collection;


@Component
@Scope("application")
public class SensorStationGardenerService {


    @Autowired
    SensorStationGardenerRepository sensorStationGardenerRepository;
    @Autowired
    UserxRepository userxRepository;
    @Autowired
    SensorStationRepository sensorStationRepository;


    public SensorStationGardener loadSensorStationGardener(String uuid) {
        return sensorStationGardenerRepository.findSensorStationGardenerById(uuid);
    }


    public SensorStationGardener save(Userx gardener, SensorStation sensorStation, SensorStationGardener sensorStationGardener) {
        gardener.getSensorStationGardener().add(sensorStationGardener);
        sensorStation.getSensorStationGardener().add(sensorStationGardener);
        sensorStationGardener.setSensorStation(sensorStation);
        sensorStationGardener.setGardener(gardener);
        return sensorStationGardenerRepository.save(sensorStationGardener);
    }


    public Collection<SensorStation> getAllSensorStationsOfUser() {
        return sensorStationGardenerRepository.getSensorStationsByGardener(getAuthenticatedUser());
    }

    private Userx getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userxRepository.findFirstByUsername(auth.getName());
    }
}
