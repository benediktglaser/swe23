package at.qe.g1t2.services;


import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.AccessPointRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.repositories.UserxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Random;


/**
 * This Class saves/delete Sensorstations and allows tho remove/swap the gardener of the stations
 */
@Component
@Scope("application")
public class SensorStationService implements Serializable {

    @Autowired
    SensorStationRepository sensorStationRepository;

    @Autowired
    UserxRepository userRepository;

    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;
    @Autowired
    AccessPointRepository accessPointRepository;

    private Random randomGenerator = new Random();


    public SensorStation loadSensorStation(String uuid) {
        return sensorStationRepository.findSensorStationById(uuid);
    }

    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN','GARDENER')")
    public SensorStation saveSensorStation(AccessPoint accessPoint, SensorStation sensorStation) {
        if (!sensorStation.isNew()) {
            return sensorStationRepository.save(sensorStation);

        }
        sensorStation.setCreateDate(LocalDateTime.now());
        sensorStation.setAccessPoint(accessPoint);
        sensorStation = sensorStationRepository.save(sensorStation);
        accessPoint.getSensorStation().add(sensorStation);
        accessPointRepository.save(accessPoint);
        return sensorStation;

    }

    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN')")
    public void deleteSensorStation(SensorStation sensorStation) {
        if (sensorStation.getAccessPoint() != null) {
            sensorStation.getAccessPoint().getSensorStation().remove(sensorStation);
            accessPointRepository.save(sensorStation.getAccessPoint());
        }
        sensorStationRepository.delete(sensorStation);
    }


    public Collection<SensorStation> getAllSensorStations() {
        return sensorStationRepository.getSensorStationByEnabledTrue();
    }


    public Page<SensorStation> getAllSensorStations(Specification<SensorStation> spec, Pageable pageable) {
        return sensorStationRepository.findAll(spec, pageable);

    }


    public SensorStation getSensorStationByAccessPointIdAndDipId(String accessPointId, Long dipId) {
        return sensorStationRepository.findSensorStationByAccessPointAndDipId((accessPointRepository.findAccessPointById(accessPointId)), dipId);
    }

    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','ADMIN')")
    public void removeSensorStationFromAccessPoint(AccessPoint accessPoint, SensorStation sensorStation) {
        accessPoint.getSensorStation().remove(sensorStation);
        accessPointRepository.save(accessPoint);
        deleteSensorStation(sensorStation);
    }

    public SensorStation getSensorStation(String mac) {
        return sensorStationRepository.getSensorStationsByMac(mac);

    }

    public String getRandomPicture(SensorStation sensorStation) {
        return sensorStation.getPictures().get(randomGenerator.nextInt(sensorStation.getPictures().size())).getPath();
    }

}
