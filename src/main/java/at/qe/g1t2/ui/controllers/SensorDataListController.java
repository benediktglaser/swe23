package at.qe.g1t2.ui.controllers;

import at.qe.g1t2.model.SensorData;
import at.qe.g1t2.model.SensorStation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Collection;
@Controller
@Scope("view")
public class SensorDataListController {

    SensorStation sensorStation;


    public Collection<SensorData> getSensorData(SensorStation sensorStation)  {


        return sensorStation.getSensorData();
    }
}
