package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller for the list-representation
 * of the sensordata.
 */
@Controller
@Scope("view")
public class SensorDataTypeListController{
    @Autowired
    private SensorDataTypeInfoService sensorDataTypeInfoService;

    @Autowired
    private SensorStationService sensorStationService;


    private SensorStation sensorStation;


    public List<SensorDataTypeInfo> getAllSensorDataTypeBySensorStation(SensorStation sensorStation){
        return sensorDataTypeInfoService.getAllSensorDataTypeInfosBySensorStation(sensorStation);
    }


    public SensorStation getSensorStation() {
        return sensorStationService.loadSensorStation(this.sensorStation.getId());
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }
}
