package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.AccessPointRole;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.AccessPointRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import org.hibernate.envers.AuditReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
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
    SensorStationRepository sensorStationRepository;


    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN')")
    @Transactional
    public AccessPoint loadAccessPoint(String uuid) {

        return accessPointRepository.findAccessPointById(uuid);
    }

    @Transactional
    public AccessPoint saveAccessPoint(AccessPoint accessPoint) {
        if (accessPoint.isNew()) {
            accessPoint.setAccessPointRole(AccessPointRole.ACCESS_POINT);
            accessPoint.setCreateDate(LocalDateTime.now());
        }
        return accessPointRepository.save(accessPoint);
    }


    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN')")
    @Transactional
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

    public Long numberOfAccessPoint(){
        return accessPointRepository.count();
    }

}
