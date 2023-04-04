package at.qe.g1t2.services;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorDataTypeInfoRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Scope("application")
public class SensorDataTypeInfoService {
    @Autowired
    private SensorDataTypeInfoRepository sensorDataTypeInfoRepository;
    @Autowired
    private SensorStationRepository sensorStationRepository;

    public SensorDataTypeInfo loadSensorDataTypeInfo(String id){
        return sensorDataTypeInfoRepository.findSensorDataTypeInfoById(id);
    }

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




    public List<SensorDataTypeInfo> getAllSensorDataTypeInfosBySensorStation(SensorStation sensorStation){
        return sensorDataTypeInfoRepository.getAllBySensorStation(sensorStation);

    }

    public List<SensorDataTypeInfo> getSensorDataTypeInfoBySensorStationAndType(SensorStation sensorStation, SensorDataType type){
        return sensorDataTypeInfoRepository.getSensorDataTypeInfoBySensorStationAndType(sensorStation,type);

    }
    public List<SensorDataTypeInfo> getSensorDataTypeInfoBySensorStationAndTypeAndCreateDateAfter(SensorStation sensorStation, SensorDataType type, LocalDateTime createDate){
        return sensorDataTypeInfoRepository.getSensorDataTypeInfoBySensorStationAndTypeAndCreateDateAfter(sensorStation,type,createDate);
    }

}

