package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.restapi.model.SensorStationDTO;
import at.qe.g1t2.services.AccessPointService;
import at.qe.g1t2.services.VisibleSensorStationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for displaying the visible SensorStations for different AccessPoints.
 */

@Controller
@Scope("view")
public class VisibleSensorStationController {
    @Autowired
    private VisibleSensorStationsService visibleSensorStationsService;
    @Autowired
    private AccessPointService accessPointService;

    public Map<AccessPoint, Map<Long, SensorStationDTO>> getVisibleMap() {
        return visibleSensorStationsService.getVisibleMap();
    }

    private Long selectedDipId;

    /**
     * This method retrieves a list of SensorStation IDs that are visible to a given AccessPoint using a map.
     * @param accessPoint
     */
    public List<Long> getSensorStationsByAccessPoint(AccessPoint accessPoint){
        Map<AccessPoint, Map<Long, SensorStationDTO>> visibleMap = visibleSensorStationsService.getVisibleMap();
        Map<Long,SensorStationDTO> sensorStationDTOs = visibleMap.get(accessPoint);
        if(sensorStationDTOs == null){
            return new ArrayList<>();
        }
        return sensorStationDTOs.keySet().stream().toList();
    }

    /**
     *  This method is used to select a SensorStationDTO object based on the AccessPoint and dipId parameters,
     *  mark it as verified, and update the visibleSensorStationsService with the updated SensorStationDTO.
     *  The selectedDipId field is also updated to the dipId parameter.
     * @param accessPoint
     * @param dipId
     */
    public void selectSensorStation(AccessPoint accessPoint, Long dipId){

        SensorStationDTO sensorStationDTO = getSensorStationsByAccessPointAndDipId(accessPoint,dipId);
        sensorStationDTO.setVerified(true);
        visibleSensorStationsService.replaceSensorStationDTO(accessPoint,dipId.toString(),sensorStationDTO);
        selectedDipId = dipId;
    }
    public SensorStationDTO getSensorStationsByAccessPointAndDipId(AccessPoint accessPoint, Long dipId){
        return visibleSensorStationsService.getSensorStationByAccessPointAndDipId(accessPoint,dipId.toString());
    }

    public void resetVisibleList(AccessPoint accessPoint){
        visibleSensorStationsService.resetVisibleList(accessPoint);
    }

    public Long getSelectedDipId() {
        return selectedDipId;
    }
}
