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

import java.util.Collection;


@Component
@Scope("application")
public class SensorStationGardenerService {


    @Autowired
    UserxRepository userxRepository;
    @Autowired
    SensorStationRepository sensorStationRepository;


    public void assignGarderner(Userx userx, SensorStation sensorStation){
        sensorStation.setGardener(userx);
        sensorStationRepository.save(sensorStation);
    }



    public Collection<SensorStation> getAllSensorStationsOfUser() {
        return sensorStationRepository.getSensorStationsByGardener(getAuthenticatedUser());
    }

    private Userx getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userxRepository.findFirstByUsername(auth.getName());
    }


}
