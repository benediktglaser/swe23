package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.Picture;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Scope("view")
public class PictureController {


    @Autowired
    PictureService pictureService;


    public List<Picture> getAllPictureBySensorStation(SensorStation sensorStation){

        return pictureService.getAllPictureBySensorStation(sensorStation);
    }

}
