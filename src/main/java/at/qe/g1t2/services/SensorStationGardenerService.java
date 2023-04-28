package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;


@Component
@Scope("application")
public class SensorStationGardenerService implements Serializable {


    @Autowired
    UserxRepository userxRepository;
    @Autowired
    SensorStationRepository sensorStationRepository;

    Set<SensorStation> gardenerIsHere = new HashSet<>();


    public void assignGardener(Userx userx, SensorStation sensorStation){
        sensorStation.setGardener(userx);
        sensorStationRepository.save(sensorStation);
    }






    public Collection<SensorStation> getAllSensorStationsOfUser() {
        return sensorStationRepository.getSensorStationsByGardenerAndEnabledTrue(getAuthenticatedUser());
    }

    private Userx getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userxRepository.findFirstByUsername(auth.getName());
    }

    public Set<SensorStation> getGardenerIsHere() {
        return gardenerIsHere;
    }

}
