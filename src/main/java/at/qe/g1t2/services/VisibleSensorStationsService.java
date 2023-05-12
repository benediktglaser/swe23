package at.qe.g1t2.services;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.restapi.model.SensorStationDTO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This service manages the SensorStations DTO which can be seen, when
 * activating the couple mode on the webserver.
 */
@Component
@Scope("application")
public class VisibleSensorStationsService {

    private Map<AccessPoint, Map<Long, SensorStationDTO>> visibleMap = new HashMap<>();


    public void addVisibleStation(AccessPoint accessPoint, SensorStationDTO sensorStationDTO) {

        if (!visibleMap.containsKey(accessPoint)) {
            Map<Long, SensorStationDTO> dipDTOMap = new HashMap<>();
            dipDTOMap.put(sensorStationDTO.getDipId(), sensorStationDTO);
            visibleMap.put(accessPoint, dipDTOMap);
        } else {
            visibleMap.get(accessPoint).put(sensorStationDTO.getDipId(), sensorStationDTO);
        }
    }

    public SensorStationDTO getSensorStationByAccessPointAndDipId(AccessPoint accessPoint, String dipId) {
        return visibleMap.get(accessPoint).get(Long.parseLong(dipId));
    }

    public void replaceSensorStationDTO(AccessPoint accessPoint, String dipId, SensorStationDTO sensorStationDTO) {
        visibleMap.get(accessPoint).put(Long.parseLong(dipId), sensorStationDTO);
    }

    public Map<AccessPoint, Map<Long, SensorStationDTO>> getVisibleMap() {
        return visibleMap;
    }

    public void setVisibleMap(Map<AccessPoint, Map<Long, SensorStationDTO>> visibleMap) {
        this.visibleMap = visibleMap;
    }

    public void resetVisibleList(AccessPoint accessPoint) {
        visibleMap.remove(accessPoint);
    }
}
