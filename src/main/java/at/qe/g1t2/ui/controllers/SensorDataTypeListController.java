package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
@Scope("view")
public class SensorDataTypeListController{
    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;

    @Autowired
    SensorStationService sensorStationService;

    private SensorStation sensorStation;

    @Transactional
    public List<SensorDataTypeInfo> getAllSensorDataTypeBySensorStation(){
        return sensorDataTypeInfoService.getAllSensorDataTypeInfosBySensorStation(sensorStation);
    }

    @Transactional
    public SensorStation getSensorStation() {
        return sensorStationService.loadSensorStation(this.sensorStation.getId());
    }

    @Transactional
    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStationService.loadSensorStation(sensorStation.getId());
    }
}
