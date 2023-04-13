package at.qe.g1t2.services;

import at.qe.g1t2.RestAPI.exception.EntityNotFoundException;
import at.qe.g1t2.model.Picture;
import at.qe.g1t2.model.SensorStation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
@SpringBootTest
class PictureServiceTest {
    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    PictureService pictureService;


    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void save() {

        Picture picture = new Picture();
        picture.setPictureName("test");
        picture.setPath("testPath");
        SensorStation sensorStation = sensorStationService.loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        int size = sensorStation.getPictures().size();
        Assertions.assertThrows(EntityNotFoundException.class,() -> pictureService.loadPicture(""));
        picture = pictureService.save(sensorStation,picture);
        picture = pictureService.loadPicture(picture.getId());
        sensorStation = sensorStationService.loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        Assertions.assertNotNull(picture);
        Assertions.assertNotNull(sensorStation);
        Assertions.assertEquals(size+1,sensorStation.getPictures().size());
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void delete() {
        Picture picture = new Picture();
        picture.setPictureName("test");
        picture.setPath("testPath");
        SensorStation sensorStation = sensorStationService.loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        int size = sensorStation.getPictures().size();
        Assertions.assertThrows(EntityNotFoundException.class,() -> pictureService.loadPicture(""));
        picture = pictureService.save(sensorStation,picture);
        System.out.println(picture.getId());
        picture = pictureService.loadPicture(picture.getId());
        sensorStation = sensorStationService.loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        Assertions.assertNotNull(picture);
        Assertions.assertNotNull(sensorStation);
        Assertions.assertEquals(size+1,sensorStation.getPictures().size());
        pictureService.delete(picture);

        sensorStation = sensorStationService.loadSensorStation("8ccfdfaa-9731-4786-8efa-e2141e5c4095");
        Assertions.assertEquals(size,sensorStation.getPictures().size());
    }
}