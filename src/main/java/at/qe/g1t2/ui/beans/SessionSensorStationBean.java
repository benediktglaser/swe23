package at.qe.g1t2.ui.beans;

import at.qe.g1t2.model.SensorStation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Component
@Scope("session")
public class SessionSensorStationBean implements Serializable {

    SensorStation sensorStation;


    public void setSensorStation(SensorStation sensorStation){
        System.out.println(sensorStation);
        this.sensorStation = sensorStation;

    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }
}
