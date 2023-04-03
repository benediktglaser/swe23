package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorStationService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.event.ToggleEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Collection;

@Controller
@Scope("view")
public class SearchViewDashboard {

    @Autowired
    private SensorStationService sensorStationService;

    private String plantName;
    private String category;


    public String getPlant() {
        return plantName;
    }

    public void setPlant(String text1) {
        this.plantName = plantName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String text2) {
        this.category = category;
    }

    /*public Collection<SensorStation> getSensorStationsByName() {
        return sensorStationService.getAllSensorStationsByName(this.plantName);
    }*/

}

