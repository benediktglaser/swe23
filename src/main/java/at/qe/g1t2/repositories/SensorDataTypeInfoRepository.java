package at.qe.g1t2.repositories;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorDataTypeInfoRepository extends JpaRepository<SensorDataTypeInfo,String>, JpaSpecificationExecutor<SensorDataTypeInfo> {

    SensorDataTypeInfo findSensorDataTypeInfoById(String id);

    List<SensorDataTypeInfo> getAllBySensorStation(SensorStation sensorStation);
    @Query("SELECT e FROM SensorDataTypeInfo e WHERE e.createDate = (SELECT MAX(e2.createDate) FROM SensorDataTypeInfo e2 where e2.sensorStation = :sensorStation and e2.type = :type)" +
            " and e.sensorStation = :sensorStation and e.type = :type")
    SensorDataTypeInfo findSensorDataTypeInfoByCreateDateMax(@Param("sensorStation") SensorStation sensorStation,@Param("type") SensorDataType type);
    List<SensorDataTypeInfo> getSensorDataTypeInfoBySensorStationAndTypeAndCreateDateAfter(SensorStation sensorStation, SensorDataType type, LocalDateTime createDate);



}
