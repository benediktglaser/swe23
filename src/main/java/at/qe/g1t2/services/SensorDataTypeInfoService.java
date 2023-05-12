package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorDataTypeInfoRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for accessing and manipulating sensorDataTypeInfo
 *
 */
@Component
@Scope("application")
public class SensorDataTypeInfoService implements Serializable {

    private final SensorDataTypeInfoRepository sensorDataTypeInfoRepository;

    private final SensorStationRepository sensorStationRepository;

    @Autowired
    public SensorDataTypeInfoService(SensorDataTypeInfoRepository sensorDataTypeInfoRepository, SensorStationRepository sensorStationRepository) {
        this.sensorDataTypeInfoRepository = sensorDataTypeInfoRepository;
        this.sensorStationRepository = sensorStationRepository;
    }

    public SensorDataTypeInfo loadSensorDataTypeInfo(String id){
        return sensorDataTypeInfoRepository.findSensorDataTypeInfoById(id);
    }

    public SensorDataTypeInfo save(SensorStation sensorStation,SensorDataTypeInfo sensorDataTypeInfo){
        if(sensorDataTypeInfo.isNew()){
            sensorDataTypeInfo.setCreateDate(LocalDateTime.now());
            sensorDataTypeInfo.setSensorStation(sensorStation);
            sensorStation.getSensorDataTypeInfos().add(sensorDataTypeInfo);
            SensorStation newSensorStation = sensorStationRepository.save(sensorDataTypeInfo.getSensorStation());
            return sensorDataTypeInfoRepository.save(sensorDataTypeInfo);
        }
        return sensorDataTypeInfoRepository.save(sensorDataTypeInfo);
    }


    public List<SensorDataTypeInfo> getAllSensorDataTypeInfosBySensorStation(SensorStation sensorStation){
        List<SensorDataTypeInfo> info = new ArrayList<>();
        for (SensorDataType sensorDataType : SensorDataType.values()) {
            info.add(sensorDataTypeInfoRepository.findSensorDataTypeInfoByCreateDateMax(sensorStation, sensorDataType));
        }
        return info;

    }

    public List<SensorDataTypeInfo> getTypeInfoByStationAndType(SensorStation sensorStation, SensorDataType type) {
        return sensorDataTypeInfoRepository.getSensorDataTypeInfosBySensorStationAndTypeOrderByCreateDate(sensorStation, type);
    }
}

