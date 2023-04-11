package at.qe.g1t2.repositories;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.Picture;
import at.qe.g1t2.model.SensorStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, String> {


    List<Picture> findBySensorStation(SensorStation sensorStation);
}
