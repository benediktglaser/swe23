package at.qe.g1t2.repositories;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for managing {@link SensorData} entities.
 */

public interface SensorDataRepository extends JpaRepository<SensorData, String>,JpaSpecificationExecutor<SensorData> {

    SensorData findSensorDataById(String uuid);

    List<SensorData> findByType(SensorDataType type);

    List<SensorData> findBySensorStationId(String id);

    List<SensorData> findSensorDataBySensorStation(SensorStation sensorStation);
    List<SensorData> findSensorDataBySensorStationAndTypeOrderByCreateDate(SensorStation sensorStation,SensorDataType type);
    List<SensorData> findSensorDataBySensorStationOrderByCreateDate(SensorStation sensorStation);

    SensorData findFirstBySensorStationAndTypeAndCreateDate(SensorStation sensorStation, SensorDataType type, LocalDateTime createDate);
}
