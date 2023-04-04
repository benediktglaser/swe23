package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.UsersFavourites;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.UsersFavouritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Collection;

@Controller
@Scope("view")
public class DashboardController {

    @Autowired
    private SensorStationService sensorStationService;

    @Autowired
    private UsersFavouritesService usersFavouritesService;

    private UsersFavourites usersFavourites;
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

    public Collection<SensorStation> getSensorStationsByName() {
        return usersFavouritesService.getAllFavouritesSensorStationsForUser();
    }

    public void removeFromUsersFavourite(){
        usersFavouritesService.removeFromUsersFavourites(this.usersFavourites);
    }
}

