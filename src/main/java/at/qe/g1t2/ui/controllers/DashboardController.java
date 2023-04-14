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

@Controller
@Scope("view")
public class DashboardController extends SensorStationListController {

    @Autowired
    private SensorStationService sensorStationService;

    @Autowired
    SessionSensorStationBean sessionSensorStationBean;

    @Autowired
    UserService userService;

    public DashboardController() {
        super();
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

    @Override
    public String redirectToSensorDataPage(SensorStation sensorStation) {
        sessionSensorStationBean.setSensorStation(sensorStation);
        return "sensorDataForUsers.xhtml?faces-redirect=true";
    }

    @Override
    public String getFrontPicture(SensorStation sensorStation){
        if(sensorStation.getPictures().isEmpty()){
            return "plant-test1.png";
        }
        return sensorStation.getPictures().get(0).getPath();
    }

}

