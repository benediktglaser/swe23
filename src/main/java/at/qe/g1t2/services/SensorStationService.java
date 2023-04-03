package at.qe.g1t2.services;


import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.AccessPointRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

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

    @Autowired
    AccessPointRepository accessPointRepository;

    @Transactional
    public SensorStation loadSensorStation(String uuid) {
        return sensorStationRepository.findSensorStationById(uuid);
    }

    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN')")
    @Transactional
    public SensorStation saveSensorStation(AccessPoint accessPoint, SensorStation sensorStation) {
        SensorStation checkSensorStation = getSensorStationByAccessPointIdAndDipId(accessPoint.getAccessPointID(), sensorStation.getDipId());
        if (checkSensorStation != null) {
            return sensorStationRepository.save(sensorStation);

        }
        sensorStation.setCreateDate(LocalDateTime.now());
        accessPoint.getSensorStation().add(sensorStation);
        sensorStation.setAccessPoint(accessPoint);
        accessPoint = accessPointRepository.save(accessPoint);

        return loadSensorStation(accessPoint
                .getSensorStation()
                .get(accessPoint.getSensorStation().size() - 1).getId());

    }

    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN')")
    @Transactional
    public void deleteSensorStation(SensorStation sensorStation) {
        sensorStationRepository.delete(sensorStation);
    }


    public Collection<SensorStation> getAllSensorStations() {
        return sensorStationRepository.findAll();

    }

    /*public Collection<SensorStation> getAllSensorStationsByName(String plantName){
        if(plantName!=null){
            return sensorStationRepository.getSensorStationsByUserAndName(getAuthenticatedUser(),plantName);
        }
        return sensorStationRepository.findAll();

    }*/

    public SensorStation getSensorStationByAccessPointIdAndDipId(String accessPointId, Long dipId) {
        return sensorStationRepository.findSensorStationByAccessPointAndDipId((accessPointRepository.findAccessPointById(accessPointId)), dipId);
    }

    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN')")
    @Transactional
    public void removeSensorStationFromAccessPoint(AccessPoint accessPoint, SensorStation sensorStation) {
        accessPoint.getSensorStation().remove(sensorStation);
        accessPointRepository.save(accessPoint);
        deleteSensorStation(sensorStation);
    }

    private Userx getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findFirstByUsername(auth.getName());
    }

}
