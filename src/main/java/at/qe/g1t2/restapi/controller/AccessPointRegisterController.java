package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.configs.WebSecurityConfig;
import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.restapi.model.AccessPointDTO;
import at.qe.g1t2.restapi.model.LoginDTO;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.LogMsg;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/accessPoint/register")
public class AccessPointRegisterController {

    @Autowired
    AccessPointService accessPointService;
    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;

    @Autowired
    SensorStationService sensorStationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessPointRegisterController.class);

    @PostMapping()
    public ResponseEntity<LoginDTO> register(@Valid @RequestBody AccessPointDTO accessPointDTO) {
        ModelMapper modelMapper = new ModelMapper();
        String password = UUID.randomUUID().toString();
        String encodedPassword = WebSecurityConfig.passwordEncoder().encode(password);
        AccessPoint newAccessPoint = modelMapper.map(accessPointDTO, AccessPoint.class);
        newAccessPoint.setPassword(encodedPassword);
        newAccessPoint.setEnabled(false);
        newAccessPoint = accessPointService.saveAccessPoint(newAccessPoint);

        LogMsg<String,AccessPoint> msg = new LogMsg<>(LogMsg.LogType.NEW_CONNECTION, AccessPoint.class,"Access point: "+newAccessPoint.getAccessPointID(),"New Access point registered",null);
        LOGGER.error(msg.getMessage());

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setId(newAccessPoint.getId());
        loginDTO.setPassword(password);
        return new ResponseEntity<>(loginDTO, HttpStatus.OK);
    }


}
