package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.AccessPointRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Scope("application")
public class SensorStationService {
    @Autowired
    SensorStationRepository sensorStationRepository;

    @Autowired
    AccessPointRepository accessPointRepository;

    public UUID saveSensorStation(AccessPoint accessPoint, SensorStation sensorStation){

        if(sensorStation.isNew()){
            LocalDateTime createDate = LocalDateTime.now();
            sensorStation.setCreateDate(createDate);
            accessPoint.addSensorStation(sensorStation);
            accessPointRepository.save(accessPoint);
            return accessPointRepository.save(accessPoint)
                    .getSensorStations()
                    .get(accessPoint.getSensorStations().size()-1).getId();
        }
        sensorStationRepository.save(sensorStation);
        return sensorStation.getId();
    }

}
