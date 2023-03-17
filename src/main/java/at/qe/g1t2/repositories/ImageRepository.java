package at.qe.g1t2.repositories;

import at.qe.g1t2.model.Image;
import at.qe.g1t2.model.SensorStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    Image findImageById(UUID uuid);

    List<Image> findBySensorStation(SensorStation sensorStation);
}
