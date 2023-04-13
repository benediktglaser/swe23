package at.qe.g1t2.RestAPI.controller;


import at.qe.g1t2.RestAPI.exception.FileUploadException;
import at.qe.g1t2.model.Picture;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.PictureService;
import at.qe.g1t2.services.SensorStationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@RestController
public class FileUploadController {

    @Autowired
    private PictureService pictureService;

    @Autowired
    private SensorStationService sensorStationService;

    @PostMapping(value = "/upload")
    public ResponseEntity<Picture> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("sensorStationId") String sensorStationId,
                                   HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                String extension = fileName.substring(fileName.lastIndexOf("."));
                String uniqueFileName = System.currentTimeMillis() + extension;
                String uploadDir = request.getServletContext().getRealPath("/resources/images");

                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdir();
                }


                File serverFile = new File(uploadDir + File.separator + uniqueFileName);
                file.transferTo(serverFile);
                SensorStation sensorStation = sensorStationService.loadSensorStation(sensorStationId);
                Picture picture = new Picture();
                picture.setPath(uniqueFileName);
                pictureService.save(sensorStation,picture);
                redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded " + file.getOriginalFilename() + "!");
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (IOException e) {
                throw new FileUploadException("File could not saved"+e.getMessage());
            }
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }


}