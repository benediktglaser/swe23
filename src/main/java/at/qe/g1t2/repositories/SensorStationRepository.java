package at.qe.g1t2.repositories;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SensorStationRepository extends JpaRepository<SensorStation, UUID> {

    SensorStation findSensorStationById(UUID uuid);

    List<SensorStation> getSensorStationsByAccessPoint(AccessPoint accessPoint);
}

