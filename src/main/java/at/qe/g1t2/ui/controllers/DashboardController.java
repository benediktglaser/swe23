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
public class DashboardController extends AbstractListController<String, SensorStation> {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionSensorStationBean sessionSensorStationBean;
    @Autowired
    private SensorStationService sensorStationService;

    public DashboardController() {
        this.setListToPageFunction((spec, page) -> sensorStationService.getAllSensorStations(spec, page));
    }


    /**
     * Method to remove the given sensorstation from the favourites page
     * of the user.
     *
     * @param sensorStation
     */
    public void removeFromUsersFavourite(SensorStation sensorStation) {

        userService.removeSensorStationToUser(sensorStation);
    }

    /**
     * Method to filter only the sensorstation which
     * belong to the given user.
     *
     * @param user
     */
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

    /**
     * This method returns a picture for each sensorstation to be
     * displayed, if no picture is present a default
     * picture will be returned.
     *
     * @param sensorStation
     * @return
     */
    public String getFrontPicture(SensorStation sensorStation) {
        if (sensorStation.getPictures().isEmpty()) {
            return "plant-test1.png";
        }
        return sensorStationService.getRandomPicture(sensorStation);
    }

}

