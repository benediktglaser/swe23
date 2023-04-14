package at.qe.g1t2.repositories;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing {@link SensorData} entities.
 */

public interface SensorDataRepository extends JpaRepository<SensorData, String>,JpaSpecificationExecutor<SensorData>,Serializable {

    SensorData findSensorDataById(String uuid);

    List<SensorData> findByType(SensorDataType type);

    List<SensorData> findBySensorStationId(String id);

    List<SensorData> findSensorDataBySensorStation(SensorStation sensorStation);
    List<SensorData> findSensorDataBySensorStationAndTypeOrderByCreateDate(SensorStation sensorStation,SensorDataType type);
    List<SensorData> findSensorDataBySensorStationOrderByCreateDate(SensorStation sensorStation);

    SensorData findFirstBySensorStationAndTypeAndCreateDate(SensorStation sensorStation, SensorDataType type, LocalDateTime createDate);

    List<SensorData> findSensorDataBySensorStationAndType(SensorStation sensorStation,SensorDataType sensorDataType);
    List<SensorData> findSensorDataBySensorStationAndCreateDateBetweenOrderByCreateDate(SensorStation sensorStation,LocalDateTime start,LocalDateTime end);
    @Query("SELECT u.timestamp, u.measurement from SensorData u where u.sensorStation = :sensorStation order by u.createDate")
    List<Object[]> getSensorDataBySensorStation(SensorStation sensorStation);
    @Query("SELECT u.timestamp, u.measurement from SensorData u where u.sensorStation = :sensorStation and u.type = :sensorDataType order by u.createDate")
    List<Object[]> getSensorDataBySensorStationAndType(@Param("sensorStation")SensorStation sensorStation,@Param("sensorDataType") SensorDataType sensorDataType);
    @Query("SELECT u.timestamp, u.measurement from SensorData u where u.sensorStation = :sensorStation and u.type = :sensorDataType and u.createDate >= :lastDate order by u.createDate")
    List<Object[]> getNewSensorDataBySensorStationAndType(@Param("sensorStation")SensorStation sensorStation,@Param("sensorDataType") SensorDataType sensorDataType, @Param("lastDate") LocalDateTime lastDate);
}
