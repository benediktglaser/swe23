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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * This REST-Interface provides method for new AccessPoints to register themselves.
 */
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

    /**
     * This method is called from new accesspoints, with their name and initial sending-interval
     * Then they will receive their username and password for the HTTP-Basic-Authentication
     *
     * @param accessPointDTO
     * @return loginDTO
     */
    @PostMapping()
    public ResponseEntity<LoginDTO> register(@Valid @RequestBody AccessPointDTO accessPointDTO) {
        ModelMapper modelMapper = new ModelMapper();
        String password = UUID.randomUUID().toString();
        String encodedPassword = WebSecurityConfig.passwordEncoder().encode(password);
        AccessPoint newAccessPoint = modelMapper.map(accessPointDTO, AccessPoint.class);
        newAccessPoint.setPassword(encodedPassword);
        newAccessPoint.setEnabled(false);
        newAccessPoint.setThresholdInterval(accessPointDTO.getSendingInterval());
        newAccessPoint.setSendingInterval(accessPointDTO.getSendingInterval());
        newAccessPoint = accessPointService.saveAccessPoint(newAccessPoint);

        LogMsg<String, AccessPoint> msg = new LogMsg<>(LogMsg.LogType.NEW_CONNECTION, AccessPoint.class, "Access point: " + newAccessPoint.getAccessPointID(), "New Access point registered", null);
        LOGGER.error(msg.getMessage());

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setId(newAccessPoint.getId());
        loginDTO.setPassword(password);

        return new ResponseEntity<>(loginDTO, HttpStatus.OK);
    }

    /**
     * This method is called from AccessPoint to check if their ID is known at the server
     *
     * @param accessPointId
     * @return boolean
     */
    @GetMapping("/credentials")
    public ResponseEntity<Boolean> checkCredentials(@RequestParam String accessPointId) {
        AccessPoint accessPoint = accessPointService.loadAccessPoint(accessPointId);
        if (accessPoint == null) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
    }
}
