package at.qe.g1t2.services;

import at.qe.g1t2.model.Picture;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.PictureRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.restapi.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * This service is responsible for managing  the saved pictures.
 */
@Service
public class PictureService {

    @Autowired
    PictureRepository pictureRepository;
    @Autowired
    SensorStationRepository sensorStationRepository;


    public Picture loadPicture(String id) {
        Optional<Picture> picture = pictureRepository.findById(id);
        if (picture.isPresent()) {
            return picture.get();
        } else throw new EntityNotFoundException("Entity Not Found");
    }


    public Picture save(SensorStation sensorStation, Picture picture) {
        if (picture.isNew()) {
            LocalDateTime createDate = LocalDateTime.now();
            picture.setCreateDate(createDate);
            picture.setSensorStation(sensorStation);
            picture = pictureRepository.save(picture);
            sensorStation.getPictures().add(picture);
            sensorStationRepository.save(sensorStation);

            return picture;
        }
        return pictureRepository.save(picture);
    }

    public void delete(Picture picture) {
        picture.getSensorStation().getPictures().remove(picture);
        sensorStationRepository.save(picture.getSensorStation());
        pictureRepository.delete(picture);


    }

    public List<Picture> getAllPictureBySensorStation(SensorStation sensorStation) {

        return pictureRepository.findBySensorStation(sensorStation);
    }

}

