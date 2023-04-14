package at.qe.g1t2.services;

import at.qe.g1t2.model.Picture;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.PictureRepository;
import at.qe.g1t2.repositories.SensorStationRepository;
import at.qe.g1t2.restapi.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public Picture save(SensorStation sensorStation, Picture picture) {
        if (picture.isNew()) {
            LocalDateTime createDate = LocalDateTime.now();
            picture.setCreateDate(createDate);
            picture.setSensorStation(sensorStation);
            sensorStation.getPictures().add(picture);
            sensorStation = sensorStationRepository.save(sensorStation);
            return sensorStation.getPictures().get(sensorStation.getPictures().size() - 1);
        }
        return pictureRepository.save(picture);
    }

    @Transactional
    public void delete(Picture picture) {
        pictureRepository.delete(picture);
        picture.getSensorStation().getPictures().remove(picture);
        sensorStationRepository.save(picture.getSensorStation());

    }

    public List<Picture> getAllPictureBySensorStation(SensorStation sensorStation) {

        return pictureRepository.findBySensorStation(sensorStation);
    }

}

