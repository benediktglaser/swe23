package at.qe.g1t2.ui.beans;

import at.qe.g1t2.model.SensorStation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Bean for managing the selected sensorstation.
 */
@Component
@Scope("session")
public class SessionSensorStationBean implements Serializable {

    private SensorStation sensorStation;

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;

    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }
}
