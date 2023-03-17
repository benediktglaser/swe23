package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.AccessPointRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
/**
 * This Class saves/delete Accesspoints and allows tho remove/add Sensorstations
 */
@Component
@Scope("application")
public class AccessPointService {

    @Autowired
    AccessPointRepository accessPointRepository;

    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    SensorStationRepository sensorStationRepository;

    @PreAuthorize("hasAuthority('ADMIN')")
    public AccessPoint loadAccessPoint(UUID uuid){

        return accessPointRepository.findAccessPointById(uuid);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    public AccessPoint saveAccessPoint(AccessPoint accessPoint){
        if(accessPoint.isNew()){
            accessPoint.setCreateDate(LocalDateTime.now());
        }
        return accessPointRepository.save(accessPoint);
    }

    /**
     *
     * Deleting AccessPoint should also represent a Disconnection for the SensorStation
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteAccessPoint(AccessPoint accessPoint){
        List<SensorStation> sensorStation = sensorStationRepository.getSensorStationsByAccessPoint(accessPoint);
        sensorStation.forEach(x -> {x.setAccessPoint(null);x.setConnected(false); sensorStationService.saveSensorStation(x);});
        accessPointRepository.delete(accessPoint);
    }

    /**
     * When adding a SensorStation,AccessPoint and SensorStation should be connected physically
     * the setConnected call is for a correct representation in the database
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void addSensorStation(AccessPoint accessPoint, SensorStation sensorStation){
        sensorStation.setConnected(true);
        sensorStation.setAccessPoint(accessPoint);
        sensorStationService.saveSensorStation(sensorStation);
    }

    /**
     * When removing a SensorStation,AccessPoint and SensorStation it is also disconnected
     */

    @PreAuthorize("hasAuthority('ADMIN')")
    public void removeSensorStation(AccessPoint accessPoint, SensorStation sensorStation){
        sensorStation.setConnected(false);
        sensorStation.setAccessPoint(null);
        sensorStationService.saveSensorStation(sensorStation);
    }

    public List<SensorStation> getAllSensorStations(AccessPoint accessPoint){
        return sensorStationRepository.getSensorStationsByAccessPoint(accessPoint);
    }

}
