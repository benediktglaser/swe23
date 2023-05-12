package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * This controller is responsible for the retrieval
 * of the sensordatatypeinfo
 */
@Controller
@Scope("session")
public class SensorDataTypeInfoController {
    @Autowired
    private SensorDataTypeInfoService sensorDataTypeInfoService;

    private SensorStation sensorStation;

    private SensorDataTypeInfo type;

    private SensorDataType sensorDataType;

    public SensorDataTypeInfo getType() {
        if(type == null){
            type = new SensorDataTypeInfo();
        }
        return type;
    }

    public List<SensorDataType> getAllTypes() {
        return Arrays.stream(SensorDataType.values()).toList();
    }

    public void setType(SensorDataTypeInfo type) {
        this.type = type;
    }


    public void save(SensorStation sensorStation) {
        type.setType(sensorDataType);
        if (type.getMaxLimit() == null || type.getMinLimit() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: The input value cannot be null. Please enter a valid value.", null));
        } else if (type.getMinLimit() >= type.getMaxLimit()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Max has to be greater than min value.", null));
        } else {
            sensorDataTypeInfoService.save(sensorStation, type);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(type.getType() + ": Min value set to " + type.getMinLimit() + " and the max value is set to " + type.getMaxLimit()));
            type = null;
        }
    }


    public SensorDataType getSensorDataType() {
        return sensorDataType;
    }

    public void setSensorDataType(SensorDataType sensorDataType) {
        this.sensorDataType = sensorDataType;
    }

    public SensorDataTypeInfoService getSensorDataTypeInfoService() {
        return sensorDataTypeInfoService;
    }

    public void setSensorDataTypeInfoService(SensorDataTypeInfoService sensorDataTypeInfoService) {
        this.sensorDataTypeInfoService = sensorDataTypeInfoService;
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }
}
