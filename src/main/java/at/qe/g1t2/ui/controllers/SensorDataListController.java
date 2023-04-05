package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.AccessPoint;
import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import at.qe.g1t2.services.CollectionToPageConverter;
import at.qe.g1t2.services.SensorDataService;
import jakarta.persistence.criteria.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import java.util.Collection;
@Controller
@Scope("view")
public class SensorDataListController extends AbstractListController<String,SensorData> {

    @Autowired
    SensorDataService sensorDataService;

    SensorStation sensorStation;

    public void filterSensorDataBySensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
        this.getExtraSpecs().add(Specification.where((root, query, criteriaBuilder) -> {
            Path<String> sensorStationId = root.get("sensorStation").get("id");
            return criteriaBuilder.equal(sensorStationId, this.sensorStation.getId());
        }));
    }

    public SensorDataListController() {
        this.setListToPageFunction(new CollectionToPageConverter<String, SensorData>() {
            @Override
            public Page<SensorData> retrieveData(Specification<SensorData> spec, Pageable page) {
                return sensorDataService.getAllSensorData(spec, page);
            }
        });
    }

    public Collection<SensorData> getSensorData(SensorStation sensorStation)  {


        return sensorStation.getSensorData();
    }
}
