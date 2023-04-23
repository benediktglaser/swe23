package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.restapi.model.SensorStationDTO;
import at.qe.g1t2.services.VisibleSensorStationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Scope("view")
public class VisibleSensorStationController {
    @Autowired
    private VisibleSensorStationsService visibleSensorStationsService;

    public Map<AccessPoint, Map<Long, SensorStationDTO>> getVisibleMap() {
        return visibleSensorStationsService.getVisibleMap();
    }

    public List<Long> getSensorStationsByAccessPoint(AccessPoint accessPoint){
        Map<AccessPoint, Map<Long, SensorStationDTO>> visibleMap = visibleSensorStationsService.getVisibleMap();
        Map<Long,SensorStationDTO> sensorStationDTOs = visibleMap.get(accessPoint);
        if(sensorStationDTOs == null){
            return new ArrayList<>();
        }
        return sensorStationDTOs.keySet().stream().toList();
    }

    public void selectSensorStation(AccessPoint accessPoint, Long dipId){
        SensorStationDTO sensorStationDTO = getSensorStationsByAccessPointAndDipId(accessPoint,dipId);
        sensorStationDTO.setVerified(true);
        visibleSensorStationsService.replaceSensorStationDTO(accessPoint,dipId.toString(),sensorStationDTO);
    }
    public SensorStationDTO getSensorStationsByAccessPointAndDipId(AccessPoint accessPoint, Long dipId){
        return visibleSensorStationsService.getSensorStationByAccessPointAndDipId(accessPoint,dipId.toString());
    }

    public void resetVisibleList(AccessPoint accessPoint){
        visibleSensorStationsService.resetVisibleList(accessPoint);
    }
}
