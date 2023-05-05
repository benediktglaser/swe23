package at.qe.g1t2.restapi.controller;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.restapi.exception.EntityNotFoundException;
import at.qe.g1t2.restapi.exception.VisibleMapException;
import at.qe.g1t2.restapi.model.LimitsDTO;
import at.qe.g1t2.restapi.model.SensorStationDTO;
import at.qe.g1t2.restapi.model.SensorStationRegisterDTO;
import at.qe.g1t2.services.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the connection and coupling of the sensorStation and the Webserver
 * Each action on the connection of the SensorStation is logged
 *
 * @Valid checks if all NotNull attributes of the DTO's have a value otherwise it throws a exception
 */


@RestController
@RequestMapping("/api/sensorStation")
public class SensorStationConnectController {


    private static final Logger LOGGER = LoggerFactory.getLogger(SensorStationConnectController.class);

    @Autowired
    private VisibleSensorStationsService visibleSensorStationsService;
    @Autowired
    private AccessPointService accessPointService;
    @Autowired
    private SensorStationService sensorStationService;

    @Autowired
    private  SensorStationGardenerService sensorStationGardenerService;

    @Autowired
    private SensorDataTypeInfoService sensorDataTypeInfoService;

    /**
     * This method handles the sensorStation which are ready to couple from the perspective of the accessPoint
     * It returns a SensorStationRegisterDTO. The DTO informs the accesspoint if the sensorStation is already coupled  or
     * if it's available
     * each request confirms the connection of the accessPoint
     *
     * @param sensorStationDTO
     * @return ResponesEntity<SensorStationRegisterDTO>
     */
    @PostMapping("/register")
    public ResponseEntity<SensorStationRegisterDTO> addVisibleSensorStations(@Valid @RequestBody SensorStationDTO sensorStationDTO) {
        ;
        AccessPoint accessPoint = getAuthAccessPoint();

        satisFyConnection(accessPoint);
        LogMsg<String, SensorStation> msg;
        SensorStation existingSensorStation = sensorStationService.getSensorStation(sensorStationDTO.getMac());
        SensorStationRegisterDTO sensorStationRegisterDTO = new SensorStationRegisterDTO();
        /*
        If the sensor station is already in the database with the same access point, which asks for register
        the response body indicates that the sensor station is not available for coupling because its already coupeld
        */
        if (existingSensorStation != null && existingSensorStation.getAccessPoint().equals(accessPoint)) {

            sensorStationRegisterDTO.setAvailable(false);
            sensorStationRegisterDTO.setAlreadyConnected(true);
            msg = new LogMsg<>(LogMsg.LogType.OTHER, SensorStation.class, "SensorStation: DipId" + existingSensorStation.getId(), "Already Connected SensorStation asked for coupling", "Access point: " + accessPoint.getAccessPointID());
            LOGGER.warn(msg.getMessage());
            return new ResponseEntity<>(sensorStationRegisterDTO, HttpStatus.OK);
        }
        /*
        If the sensor station is registered with a different access point,
        the response body indicates that the sensor station is not available for registration
         */
        if (existingSensorStation != null) {
            sensorStationRegisterDTO.setAvailable(false);
            sensorStationRegisterDTO.setAlreadyConnected(false);
            msg = new LogMsg<>(LogMsg.LogType.OTHER, SensorStation.class, "SensorStation: DipId" + existingSensorStation.getId(), "Already Connected SensorStation with different AccessPoint asked for coupling", "Access point: " + accessPoint.getAccessPointID());
            LOGGER.warn(msg.getMessage());
            return new ResponseEntity<>(sensorStationRegisterDTO, HttpStatus.OK);
        }
        sensorStationDTO.setConnected(false);
        sensorStationDTO.setVerified(false);
        sensorStationRegisterDTO.setAvailable(true);
        sensorStationRegisterDTO.setAlreadyConnected(false);
        visibleSensorStationsService.addVisibleStation(accessPoint, sensorStationDTO);
        msg = new LogMsg<>(LogMsg.LogType.NEW_CONNECTION, SensorStation.class, "Dip Id: " + "SensorStation: DipId" + sensorStationDTO.getDipId(), "AccessPoint sends visible SensorStation for coupling", "Access point: " + accessPoint.getAccessPointID());
        LOGGER.info(msg.getMessage());
        return new ResponseEntity<>(sensorStationRegisterDTO, HttpStatus.OK);

    }

    /**
     * This endpoint checks if the SensorStation with the given dipId is verified for coupling.
     * The dipId is with the accessPointId a unique identifier for the SensorStation
     *
     * @param dipId
     * @return
     */
    @GetMapping("/verified/{dipId}")
    public ResponseEntity<Boolean> checkVerifiedSensorStationsForCouple(@PathVariable String dipId) {
        AccessPoint accessPoint = getAuthAccessPoint();
        SensorStationDTO sensorStationDTO = visibleSensorStationsService.getSensorStationByAccessPointAndDipId(accessPoint, dipId);
        satisFyConnection(accessPoint);
        return new ResponseEntity<>(sensorStationDTO.getVerified(), HttpStatus.OK);
    }

    /**
     * This endpoint informs the accessPoint if the sensorStation wit the given DipId is enabled
     * each request confirms the connection of the accessPoint and SensorStation
     *
     * @param dipId
     * @return
     */
    @GetMapping("/enabled/{dipId}")
    public ResponseEntity<Boolean> checkEnabled(@PathVariable String dipId) {

        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(getAuthAccessPoint().getId(), Long.parseLong(dipId));
        satisFyConnection(getAuthAccessPoint(), dipId);
        LogMsg<String, SensorStation> msg = new LogMsg<>(LogMsg.LogType.OTHER, SensorStation.class, "SensorStation: DipId" + sensorStation.getDipId(), "AccessPoint asks if SensorStation with DipId " + sensorStation.getId() + " is enabled", "Access point: " + getAuthAccessPoint().getAccessPointID());
        LOGGER.info(msg.getMessage());
        return new ResponseEntity<>(sensorStation.getEnabled(), HttpStatus.OK);
    }

    /**
     * This method is the last step in the coupling mode. If the accesspoint is sending a Request
     * the sensorStation will be created and stored in the database.
     * If the sensorStation exists, the connection attribute will be updated
     *
     * @param dipId
     * @return
     */
    @GetMapping("/connected/{dipId}")
    public ResponseEntity<Boolean> connect(@PathVariable String dipId) {
        AccessPoint accessPoint = getAuthAccessPoint();
        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(accessPoint.getAccessPointID(), Long.parseLong(dipId));
        if (sensorStation != null) {
            satisFyConnection(accessPoint, dipId);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        if(visibleSensorStationsService.getVisibleMap().isEmpty()){
            throw new VisibleMapException("SensorStation not registered");
        }
        SensorStationDTO sensorStationDTO = visibleSensorStationsService.getSensorStationByAccessPointAndDipId(accessPoint, dipId);
        if (sensorStationDTO == null) {
            throw new VisibleMapException("SensorStation not verified or registered");
        }
        SensorStation newSensorStation = new SensorStation();
        newSensorStation.setDipId(sensorStationDTO.getDipId());
        newSensorStation.setMac(sensorStationDTO.getMac());
        newSensorStation.setLastConnectedDate(LocalDateTime.now());
        newSensorStation.setEnabled(false);
        newSensorStation = sensorStationService.saveSensorStation(accessPoint, newSensorStation);
        sensorStationDTO.setConnected(true);
        visibleSensorStationsService.replaceSensorStationDTO(accessPoint, dipId, sensorStationDTO);
        LogMsg<String, SensorStation> msg = new LogMsg<>(LogMsg.LogType.CONNECTED, SensorStation.class, "SensorStation: DipId" + newSensorStation.getDipId(), "connected successfully", "Access point: " + getAuthAccessPoint().getAccessPointID());
        LOGGER.info(msg.getMessage());
        return new ResponseEntity<>(newSensorStation.getConnected(), HttpStatus.OK);
    }

    /**
     * This endpoint has two functionalities:
     * On the one hand it informs the accessPoint about new Limits for the sensors and on the other
     * hand each request confirms the connection of the accessPoint and SensorStation
     *
     * @param dipId
     * @return
     */
    @GetMapping("/limits/{dipId}")
    public ResponseEntity<List<LimitsDTO>> checkIfAccessPointIsConnectedAndUpdateLimits(@PathVariable String dipId) {
        AccessPoint accessPoint = getAuthAccessPoint();
        SensorStation sensorStation = accessPointService.getSensorStationByAccessPointIdAndDipId(accessPoint.getAccessPointID(), Long.parseLong(dipId));
        satisFyConnection(accessPoint, dipId);
        List<LimitsDTO> limitsDTOS = new ArrayList<>();
        sensorDataTypeInfoService.getAllSensorDataTypeInfosBySensorStation(sensorStation).forEach(x -> {
            if (x != null) {
                LimitsDTO limitsDTO = new LimitsDTO();
                limitsDTO.setMaxLimit(x.getMaxLimit());
                limitsDTO.setMinLimit(x.getMinLimit());
                limitsDTO.setDataType(x.getType().name());
                limitsDTOS.add(limitsDTO);
            }
        });
        LogMsg<String, SensorStation> msg = new LogMsg<>(LogMsg.LogType.OTHER, SensorStation.class, "SensorStation: DipId" + sensorStation.getDipId(), "Accesspoint asked for Limits for SensorStation: ", "Access point: " + getAuthAccessPoint().getAccessPointID());
        LOGGER.info(msg.getMessage());
        return new ResponseEntity<>(limitsDTOS, HttpStatus.OK);


    }

    /**
     * This method is a helpmethod for getting the id of the requesting accessPoint
     *
     * @return
     */
    public AccessPoint getAuthAccessPoint() {
        String accessPointId = SecurityContextHolder.getContext().getAuthentication().getName();
        AccessPoint accessPoint = accessPointService.loadAccessPoint(accessPointId);
        if (accessPoint == null) {
            throw new EntityNotFoundException("AccessPoint is not registered");
        }
        LogMsg<String, AccessPoint> msg = new LogMsg<>(LogMsg.LogType.OTHER, AccessPoint.class, "Access point: " + accessPoint.getAccessPointID(), "Call get getAuthAccessPoint()", "Access point: " + accessPoint.getAccessPointID());
        LOGGER.warn(msg.getMessage());
        return accessPoint;
    }
    @GetMapping("/gardenerHere/{dipId}")
    public HttpStatus checkIfGardenerIsByStation(@PathVariable String dipId){
        AccessPoint accessPoint = getAuthAccessPoint();
        SensorStation sensorStation = sensorStationService.getSensorStationByAccessPointIdAndDipId(accessPoint.getAccessPointID(),Long.parseLong(dipId));
        sensorStationGardenerService.getGardenerIsHere().add(sensorStation);
        return HttpStatus.OK;
    }



    /**
     * this method confirms the webserver that the accesspoint and sensorStation is connected.
     *
     * @param accessPoint
     * @param dipId
     */
    public void satisFyConnection(AccessPoint accessPoint, String dipId) {
        if (accessPoint == null) {
            throw new EntityNotFoundException("AccessPoint is not registered");
        }
        accessPoint.setLastConnectedDate(LocalDateTime.now());
        accessPointService.saveAccessPoint(accessPoint);
        SensorStation sensorStation = accessPointService.getSensorStationByAccessPointIdAndDipId(accessPoint.getAccessPointID(), Long.parseLong(dipId));
        if (sensorStation == null) {
            throw new EntityNotFoundException("SensorStation does not exist in database. You have to first connect the Sensor Station");

        }
        sensorStation.setLastConnectedDate(LocalDateTime.now());
        sensorStationService.saveSensorStation(accessPoint, sensorStation);
    }

    /**
     * this method confirms the webserver that the accesspoint is connected.
     *
     * @param accessPoint
     */
    public void satisFyConnection(AccessPoint accessPoint) {
        if (accessPoint == null) {
            throw new EntityNotFoundException("AccessPoint is not registered");
        }
        accessPoint.setLastConnectedDate(LocalDateTime.now());
        accessPointService.saveAccessPoint(accessPoint);
    }


}