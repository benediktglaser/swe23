package at.qe.g1t2.ui.controllers;


import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.model.Userx;
import at.qe.g1t2.services.*;
import at.qe.g1t2.ui.beans.SessionSensorStationBean;
import jakarta.persistence.criteria.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


@Component
@Scope("view")
public class SensorStationListController extends AbstractListController<String, SensorStation> {
    @Autowired
    SensorStationService sensorStationService;

    @Autowired
    SensorDataService sensorDataService;

    @Autowired
    SensorStationGardenerService sensorStationGardenerService;

    @Autowired
    SessionSensorStationBean sessionSensorStationBean;
    @Autowired
    UserService userService;

    private AccessPoint accessPoint;
    @Autowired
    AccessPointService accessPointService;

    public SensorStationListController() {
        this.setListToPageFunction((spec, page) -> sensorStationService.getAllSensorStations(spec, page));
    }

    public void filterSensorStationsByAccessPoint(AccessPoint accessPoint) {
        this.accessPoint = accessPoint;
        this.getExtraSpecs().add(Specification.where((root, query, criteriaBuilder) -> {
            Path<String> accessPointId = root.get("accessPoint").get("id");
            return criteriaBuilder.equal(accessPointId, this.accessPoint.getAccessPointID());
        }));
    }


    public void filterSensorStationsByGardner(Userx gardener) {
        this.getExtraSpecs().add(Specification.where((root, query, criteriaBuilder) -> {
            Path <String> gardenerId = root.get("gardener").get("username");
            return criteriaBuilder.equal(gardenerId,gardener.getUsername());
        }));
    }


    public void setAccessPoint(AccessPoint accessPoint) {
        this.accessPoint = accessPoint;
    }

    public String redirectToSensorDataPage(SensorStation sensorStation) {
        sessionSensorStationBean.setSensorStation(sensorStation);
        return "sensorData.xhtml?faces-redirect=true";
    }

    public String redirectToGallery(SensorStation sensorStation) {
        sessionSensorStationBean.setSensorStation(sensorStation);
        return "gallery.xhtml?faces-redirect=true";
    }


    public AccessPoint getAccessPoint() {
        return accessPoint;
    }

    public Collection<SensorStation> getAllSensorStationByOwner(){
        return sensorStationGardenerService.getAllSensorStationsOfUser();
    }

    public String getFrontPicture(SensorStation sensorStation){
        if(sensorStation.getPictures().isEmpty()){
            return "plant-test1.png";
        }
        return sensorStation.getPictures().get(0).getPath();
    }

    public List<SensorStation> getAllNewSensorStation(){
        return sensorStationService.getAllNewSensorStations(accessPoint);
    }




}


