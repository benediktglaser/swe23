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
import java.util.Collection;
import java.util.List;

/**
 * This Class saves/delete Accesspoints and allows tho remove/add Sensorstations
 */
@Component
@Scope("application")
public class AccessPointService {

    @Autowired
    AccessPointRepository accessPointRepository;

    @Autowired
    SensorStationRepository sensorStationRepository;

    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN')")
    public AccessPoint loadAccessPoint(String uuid) {

        return accessPointRepository.findAccessPointById(uuid);
    }

    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN')")
    public AccessPoint saveAccessPoint(AccessPoint accessPoint) {
        if (accessPoint.isNew()) {
            accessPoint.setCreateDate(LocalDateTime.now());
        }
        return accessPointRepository.save(accessPoint);
    }


    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN')")
    public void deleteAccessPoint(AccessPoint accessPoint) {
        accessPointRepository.delete(accessPoint);
    }


    public List<SensorStation> getAllSensorStations(AccessPoint accessPoint) {
        return sensorStationRepository.getSensorStationsByAccessPoint(accessPoint);
    }

    public Collection<AccessPoint> getAllAccessPoints() {
        return accessPointRepository.findAll();
    }

    public SensorStation getSensorStationByAccessPointIdAndDipId(String accessPointId, Long dipId) {
        return sensorStationRepository.findSensorStationByAccessPointAndDipId(loadAccessPoint(accessPointId), dipId);
    }

    public AccessPoint findAccessPointByUsername(String username){
        return accessPointRepository.findAccessPointByUsername(username);
    }
}
