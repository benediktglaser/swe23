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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("application")
public class SensorDataTypeInfoService {
    @Autowired
    private SensorDataTypeInfoRepository sensorDataTypeInfoRepository;
    @Autowired
    private SensorStationRepository sensorStationRepository;
    @Transactional
    public SensorDataTypeInfo loadSensorDataTypeInfo(String id){
        return sensorDataTypeInfoRepository.findSensorDataTypeInfoById(id);
    }
    @Transactional
    public SensorDataTypeInfo save(SensorStation sensorStation,SensorDataTypeInfo sensorDataTypeInfo){
        if(sensorDataTypeInfo.isNew()){
            sensorDataTypeInfo.setCreateDate(LocalDateTime.now());
            sensorDataTypeInfo.setSensorStation(sensorStation);
            sensorStation.getSensorDataTypeInfos().add(sensorDataTypeInfo);
            SensorStation newSensorStation = sensorStationRepository.save(sensorDataTypeInfo.getSensorStation());

            return loadSensorDataTypeInfo(newSensorStation
                    .getSensorDataTypeInfos()
                    .get(newSensorStation
                            .getSensorDataTypeInfos().size()-1).getId());
        }
        return sensorDataTypeInfoRepository.save(sensorDataTypeInfo);
    }
    @Transactional
    public Page<SensorDataTypeInfo> getAllSensorDataTypeInfos(Specification<SensorDataTypeInfo> spec, Pageable pageable) {
        return sensorDataTypeInfoRepository.findAll(spec,pageable);

    }

    @Transactional
    public List<SensorDataTypeInfo> getAllSensorDataTypeInfosBySensorStation(SensorStation sensorStation){
        List<SensorDataTypeInfo> info = new ArrayList<>();
        for(SensorDataType sensorDataType: SensorDataType.values()){
            info.add(sensorDataTypeInfoRepository.findSensorDataTypeInfoByCreateDateMax(sensorStation,sensorDataType));
        }
        return info;

    }
    @Transactional
    public List<SensorDataTypeInfo> getSensorDataTypeInfoBySensorStationAndTypeAndCreateDateAfter(SensorStation sensorStation, SensorDataType type, LocalDateTime createDate){
        return sensorDataTypeInfoRepository.getSensorDataTypeInfoBySensorStationAndTypeAndCreateDateAfter(sensorStation,type,createDate);
    }

    public Object[] getSensorDataTypInfoLimits(String id) {

        return sensorDataTypeInfoRepository.getTypeInfoById(id);

    }

    public List<SensorDataTypeInfo> getTypeInfoByStationAndType(SensorStation sensorStation, SensorDataType type){
        return sensorDataTypeInfoRepository.getSensorDataTypeInfosBySensorStationAndTypeOrderByCreateDate(sensorStation,type);
    }
}

