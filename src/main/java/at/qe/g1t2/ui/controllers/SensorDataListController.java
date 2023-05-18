package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.SensorDataService;
import at.qe.g1t2.services.SensorDataTypeInfoService;
import jakarta.persistence.criteria.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

/**
 * This controller is responsible for the correct
 * representation of the sensordata on the webserver.
 */
@Controller
@Scope("view")
public class SensorDataListController extends AbstractListController<String, SensorData> {

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private SensorDataTypeInfoService sensorDataTypeInfoService;

    private SensorStation sensorStation;


    public void filterSensorDataBySensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
        this.getExtraSpecs().add(Specification.where((root, query, criteriaBuilder) -> {
            Path<String> sensorStationId = root.get("sensorStation").get("id");
            return criteriaBuilder.equal(sensorStationId, this.sensorStation.getId());
        }));
    }

    public SensorDataListController() {
        this.setListToPageFunction((spec, page) -> sensorDataService.getAllSensorData(spec, page));
    }

    public String checkLimit(SensorData sensorData){
        if(sensorData.getMinLimit() == null || sensorData.getMaxLimit() == null){
            return "white";
        }

        if(sensorData.getMeasurement() > sensorData.getMinLimit() && sensorData.getMeasurement() < sensorData.getMaxLimit()){
            return "green";
        }
        return "red";
    }

}
