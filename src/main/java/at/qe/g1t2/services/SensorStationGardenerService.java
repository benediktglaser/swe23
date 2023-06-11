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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This service provides methods for assigning a gardener to a sensorstation
 */
@Component
@Scope("application")
public class SensorStationGardenerService implements Serializable {


    @Autowired
    private UserxRepository userxRepository;

    @Autowired
    private SensorStationRepository sensorStationRepository;

    private Set<SensorStation> gardenerIsHere = new HashSet<>();


    public void assignGardener(Userx userx, SensorStation sensorStation) {
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

    /**
     * This method returns a set of all sensorstations where the button has been pressed,
     * indicating that a gardener is on site.
     *
     * @return Set<SensorStation>
     */
    public Set<SensorStation> getGardenerIsHere() {
        return gardenerIsHere;
    }

}
