package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import jakarta.annotation.PostConstruct;
import org.jboss.logging.annotations.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Controller
@Scope("view")
public class SensorDataTypeInfoController {


    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;

    private SensorDataTypeInfo type;

    private SensorDataType sensorDataType;
    @Transactional
    public void resetSensorDataTypeInfoController(){
        sensorDataType = null;
    }
    @Transactional
    public SensorDataTypeInfo getType() {
        if(type == null){
            type = new SensorDataTypeInfo();
        }
        return type;
    }

    public List<SensorDataType> getAllTypes(){
        return Arrays.stream(SensorDataType.values()).toList();
    }
    public void setType(SensorDataTypeInfo type) {
        this.type = type;
    }
    @Transactional
    public void save(SensorStation sensorStation){

        type.setType(sensorDataType);

        sensorDataTypeInfoService.save(sensorStation,type);

        type = null;
    }
    @Transactional
    public SensorDataType getSensorDataType() {
        return sensorDataType;
    }

    @Transactional
    public void setSensorDataType(SensorDataType sensorDataType) {
        this.sensorDataType = null;
        this.sensorDataType = sensorDataType;
    }

}
