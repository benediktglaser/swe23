package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.repositories.UserxRepository;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorStationService;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@Component
@Scope("view")
public class SensorStationListController {
    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    SensorDataService sensorDataService;

    public Collection<SensorStation> getAllOwnSensorStations(){

        return sensorStationService.getAllOwnSensorStations();
    }

    public Collection<SensorStation> getAllSensorStations(){

        return sensorStationService.getAllSensorStations();
    }



    public String redirectToPage(UUID id) {
        String deckID = id.toString();
        String encoded = new BCryptPasswordEncoder().encode(id.toString());
        return "sensorData.xhtml?faces-redirect=true&id=" + deckID + "&c=" + encoded;
    }

    public Collection<SensorData> getSensorData() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();

        String id = externalContext.getRequestParameterMap().get("id");
        String encoded = externalContext.getRequestParameterMap().get("c");
        if (!new BCryptPasswordEncoder().matches(id, encoded)) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("welcome.xhtml");
        }
        SensorStation sensorStation = sensorStationService.loadSensorStation(UUID.fromString(id));
        return sensorDataService.getAllSensorDataByStation(sensorStation.getId().toString());
    }
}
