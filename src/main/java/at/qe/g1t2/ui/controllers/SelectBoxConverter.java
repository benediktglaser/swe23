package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorDataTypeInfo;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import at.qe.g1t2.services.SensorStationService;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
@FacesConverter("selectBoxConverter")
public class SelectBoxConverter implements Converter<SensorDataTypeInfo> {
    @Autowired
    SensorDataTypeInfoService sensorDataTypeInfoService;
    @Override
    public SensorDataTypeInfo getAsObject(FacesContext context, UIComponent component, String value) {
        return sensorDataTypeInfoService.loadSensorDataTypeInfo(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, SensorDataTypeInfo value) {
        return value.getId();
    }
}
