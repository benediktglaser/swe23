package at.qe.g1t2.repositories;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorDataTypeInfoRepository extends JpaRepository<SensorDataTypeInfo,String> {

    SensorDataTypeInfo findSensorDataTypeInfoById(String id);

    List<SensorDataTypeInfo> getAllBySensorStation(SensorStation sensorStation);

    List<SensorDataTypeInfo> getSensorDataTypeInfoBySensorStationAndType(SensorStation sensorStation, SensorDataType type);
    List<SensorDataTypeInfo> getSensorDataTypeInfoBySensorStationAndTypeAndCreateDateAfter(SensorStation sensorStation, SensorDataType type, LocalDateTime createDate);


}
