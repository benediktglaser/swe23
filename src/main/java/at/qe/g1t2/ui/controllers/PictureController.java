package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.Picture;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.PictureService;
import at.qe.g1t2.services.SensorStationGardenerService;
import at.qe.g1t2.services.SensorStationService;
import jakarta.servlet.ServletContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Scope("view")
public class PictureController {


    @Autowired
    PictureService pictureService;


    public List<Picture> getAllPictureBySensorStation(SensorStation sensorStation){

        return pictureService.getAllPictureBySensorStation(sensorStation);
    }

}
