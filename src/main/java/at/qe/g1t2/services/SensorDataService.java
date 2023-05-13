package at.qe.g1t2.services;


import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorDataRepository;
import at.qe.g1t2.repositories.SensorDataTypeInfoRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Service for accessing and sensor data from the access point
 */
@Component
@Scope("application")
public class SensorDataService implements Serializable {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private SensorDataTypeInfoRepository sensorDataTypeInfoRepository;
    @Autowired
    private SensorStationRepository sensorStationRepository;


    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','GARDENER')")
    public SensorData loadSensorData(String uuid) {
        return sensorDataRepository.findSensorDataById(uuid);
    }

    @PreAuthorize("hasAnyAuthority('ACCESS_POINT','GARDENER')")
    public SensorData saveSensorData(SensorStation sensorStation, SensorData sensorData) {
        if (sensorData.isNew()) {
            if(sensorData.getTimestamp()==null){
                sensorData.setTimestamp(LocalDateTime.now());
            }
            SensorDataTypeInfo sensorDataTypeInfo = sensorDataTypeInfoRepository.findSensorDataTypeInfoByCreateDateMax(sensorStation,sensorData.getType());
            if(sensorDataTypeInfo == null){
                sensorData.setLimitDate(null);
                sensorData.setMinLimit(null);
                sensorData.setMaxLimit(null);
            }
            else{
                sensorData.setLimitDate(sensorDataTypeInfo.getCreateDate());
                sensorData.setMinLimit(sensorDataTypeInfo.getMinLimit());
                sensorData.setMaxLimit(sensorDataTypeInfo.getMaxLimit());
            }

            LocalDateTime createDate = LocalDateTime.now();
            sensorData.setCreateDate(createDate);
            sensorData.setSensorStation(sensorStation);
            sensorData = sensorDataRepository.save(sensorData);
            sensorStation.getSensorData().add(sensorData);
            sensorStation = sensorStationRepository.save(sensorStation);
            return sensorData;
        }

        return sensorDataRepository.save(sensorData);
    }

    public Page<SensorData> getAllSensorData(Specification<SensorData> spec, Pageable page){
        return sensorDataRepository.findAll(spec,page);
    }


    public Collection<SensorData> getAllSensorDataByStation(String uuid) {
        return sensorDataRepository.findBySensorStationId(uuid);
    }

    public Collection<SensorData> getAllSensorDataByType(SensorDataType type) {
        return sensorDataRepository.findByType(type);
    }
    public Collection<SensorData> getAllSensorDataBySensorStationType(SensorStation sensorStation,SensorDataType type) {
        return sensorDataRepository.findSensorDataBySensorStationAndType(sensorStation,type);
    }

    public List<Object[]> getAllSensorDataByStationForChart(SensorStation sensorStation){
        return sensorDataRepository.getSensorDataBySensorStation(sensorStation);
    }

    public List<Object[]> getAllSensorDataByStationAndTypeForChart(SensorStation sensorStation, SensorDataType sensorDataType){
        return sensorDataRepository.getSensorDataBySensorStationAndType(sensorStation,sensorDataType);
    }
    public List<Object[]> getAllNewSensorDataByStationAndTypeForChart(SensorStation sensorStation, SensorDataType sensorDataType,LocalDateTime lastDate){
        return sensorDataRepository.getNewSensorDataBySensorStationAndType(sensorStation,sensorDataType,lastDate);
    }
}