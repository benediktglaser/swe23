package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.Picture;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.restapi.exception.FileUploadException;
import at.qe.g1t2.services.PictureService;
import jakarta.faces.context.FacesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * This class is used to retrieve the pictures for the photo-gallery,
 * specific for every sensorstation
 */
@Controller
@Scope("view")
public class PictureController {


    @Autowired
    private PictureService pictureService;


    public List<Picture> getAllPictureBySensorStation(SensorStation sensorStation) {
        return pictureService.getAllPictureBySensorStation(sensorStation);
    }

    public void deletePicture(Picture picture){
        String uploadDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images");
        Path filePath = Paths.get(uploadDir, picture.getPath());

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new FileUploadException("File could not be deleted: " + e.getMessage());
        }
        pictureService.delete(picture);
    }
}
