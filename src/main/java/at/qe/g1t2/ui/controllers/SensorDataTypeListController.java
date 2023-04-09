package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import jakarta.faces.component.behavior.Behavior;
import jakarta.faces.component.behavior.ClientBehaviorBase;
import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.RequestContext;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

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
        this.sensorStation = sensorStationService.loadSensorStation(sensorStation.getId());;
    }
}
