package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.UsersFavourites;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.UsersFavouritesService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Controller
@Scope("view")
public class DashboardController {

    @Autowired
    private SensorStationService sensorStationService;

    @Autowired
    SessionSensorStationBean sessionSensorStationBean;

    @Autowired
    private UsersFavouritesService usersFavouritesService;

    private UsersFavourites usersFavourites;
    private SensorStation sensorStation;

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }

    public Collection<SensorStation> getSensorStationsByName() {
        return usersFavouritesService.getAllFavouritesSensorStationsForUser();
    }

    public void removeFromUsersFavourite(SensorStation sensorStation){
        System.out.println(sensorStation);
        usersFavouritesService.removeFromUsersFavourites(sensorStation);
    }


    public String redirectToSensorDataPage(SensorStation sensorStation) {
        sessionSensorStationBean.setSensorStation(sensorStation);
        return "sensorDataForUsers.xhtml?faces-redirect=true";
    }

}

