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
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    private UserxRepository userRepository;
    @Autowired
    AccessPointRepository accessPointRepository;

    @PreAuthorize("hasAuthority('ADMIN')")
    public SensorStation loadSensorStation(UUID uuid){

        return sensorStationRepository.findSensorStationById(uuid);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public SensorStation saveSensorStation(AccessPoint accessPoint, SensorStation sensorStation){

        if(sensorStation.isNew()){
            LocalDateTime createDate = LocalDateTime.now();
            sensorStation.setCreateDate(createDate);
            accessPoint.addSensorStation(sensorStation);
            accessPointRepository.save(accessPoint);
            return accessPointRepository.save(accessPoint)
                    .getSensorStations()
                    .get(accessPoint.getSensorStations().size()-1);
        }
        sensorStationRepository.save(sensorStation);
        return sensorStation;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteSensorStation(SensorStation sensorStation){
        sensorStation.getAccessPoint().getSensorStations().remove(sensorStation);
        sensorStationRepository.delete(sensorStation);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public void removeGardener(SensorStation sensorStation){
        sensorStation.setGardener(null);
        saveSensorStation(sensorStation.getAccessPoint(),sensorStation);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public void replaceGardener(SensorStation sensorStation, Userx user){
        sensorStation.setGardener(user);
        saveSensorStation(sensorStation.getAccessPoint(),sensorStation);
    }

}
