package at.qe.g1t2.repositories;

import at.qe.g1t2.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * Repository for managing {@link SensorData} entities.
 */

public interface SensorDataRepository extends JpaRepository<SensorData, UUID> {

    SensorData findSensorDataById(UUID uuid);

    List<SensorData> findByType(String type);

    List<SensorData> findBySensorStationId(UUID id);


}
