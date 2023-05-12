package at.qe.g1t2.ui.controllers;


import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.SensorStationService;
import at.qe.g1t2.services.UserService;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import java.util.Random;

@Controller
@Scope("view")
public class DashboardController extends AbstractListController<String, SensorStation>{

    @Autowired
    UserService userService;

    @Autowired
    SessionSensorStationBean sessionSensorStationBean;
    @Autowired
    SensorStationService sensorStationService;

    public DashboardController() {
        this.setListToPageFunction((spec, page) -> sensorStationService.getAllSensorStations(spec, page));
    }



    public void removeFromUsersFavourite(SensorStation sensorStation){

        userService.removeSensorStationToUser(sensorStation);
    }

    public void filterSensorStationsByUser(Userx user) {
        this.getExtraSpecs().add(Specification.where((root, query, criteriaBuilder) -> {
            Join<SensorStation, Userx> userJoin = root.join("userx", JoinType.LEFT);
            return criteriaBuilder.equal(userJoin.get("username"), user.getId());
        }));

    }


    public String redirectToSensorDataPage(SensorStation sensorStation) {
        sessionSensorStationBean.setSensorStation(sensorStation);
        return "sensorDataForUsers.xhtml?faces-redirect=true";
    }


    public String getFrontPicture(SensorStation sensorStation){
        if(sensorStation.getPictures().isEmpty()){
            return "plant-test1.png";
        }
        return sensorStationService.getRandomPicture(sensorStation);
    }

}

