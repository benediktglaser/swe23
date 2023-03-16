package at.qe.g1t2.repositories;

import at.qe.g1t2.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface SensorDataRepository extends JpaRepository<SensorData, UUID> {

    List<SensorData> findByValue(float value);

    List<SensorData> findByType(String type);



}
