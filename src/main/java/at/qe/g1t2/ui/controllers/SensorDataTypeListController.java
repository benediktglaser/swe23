package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.repositories.SensorDataTypeInfoRepository;
import at.qe.g1t2.services.CollectionToPageConverter;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
@Scope("view")
public class SensorDataTypeListController{


    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;


    private SensorStation sensorStation;
    @Transactional

    public List<SensorDataTypeInfo> getAllSensorDataTypeBySensorStation(){

        return sensorDataTypeInfoService.getAllSensorDataTypeInfosBySensorStation(sensorStation);
    }
    @Transactional
    public SensorStation getSensorStation() {
        return sensorStation;
    }
    @Transactional
    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }
}
