package at.qe.g1t2.RestAPI.controller;


import at.qe.g1t2.RestAPI.exception.EntityNotFoundException;
import at.qe.g1t2.RestAPI.model.ResponsePicture;
import at.qe.g1t2.model.Picture;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.PictureService;
import at.qe.g1t2.services.SensorStationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;


@RestController
public class FileUploadController {

    @Autowired
    private PictureService pictureService;

    @Autowired
    private SensorStationService sensorStationService;

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("sensorStationId") String sensorStationId,
                                   HttpServletRequest request) {
        if (!file.isEmpty()) {
            try {
                // Get the file name and extension
                String fileName = file.getOriginalFilename();
                String extension = fileName.substring(fileName.lastIndexOf("."));

                // Create a unique file name to avoid conflicts
                String uniqueFileName = System.currentTimeMillis() + extension;

                // Get the path to the directory where the file should be saved
                String uploadDir = request.getServletContext().getRealPath("/resources/images");

                // Create the upload directory if it doesn't already exist
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdir();
                }

                // Save the file to the upload directory
                File serverFile = new File(uploadDir + File.separator + uniqueFileName);
                file.transferTo(serverFile);
                SensorStation sensorStation = sensorStationService.loadSensorStation(sensorStationId);
                Picture picture = new Picture();
                picture.setPath(uniqueFileName);
                pictureService.save(sensorStation,picture);
                return "redirect:/success";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/error";
    }

}