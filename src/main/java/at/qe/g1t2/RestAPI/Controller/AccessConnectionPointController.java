package at.qe.g1t2.RestAPI.Controller;

import at.qe.g1t2.RestAPI.model.AccessPointDTO;
import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.services.AccessPointService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accessPoint")
public class AccessConnectionPointController {

    @Autowired
    AccessPointService accessPointService;

    @PostMapping
    public ResponseEntity<AccessPointDTO> createAccessPoint(@Valid @RequestBody AccessPointDTO accessPoint) {
        ModelMapper modelMapper = new ModelMapper();
        AccessPoint newAccessPoint = modelMapper.map(accessPoint,AccessPoint.class);
        newAccessPoint = accessPointService.saveAccessPoint(newAccessPoint);
        accessPoint.setId(newAccessPoint.getId());
        return new ResponseEntity<>(accessPoint, HttpStatus.OK);

    }




}
