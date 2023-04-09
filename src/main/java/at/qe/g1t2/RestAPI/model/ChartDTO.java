package at.qe.g1t2.RestAPI.model;

import at.qe.g1t2.model.SensorDataType;
import at.qe.g1t2.model.SensorStation;
import jakarta.validation.constraints.NotNull;

public class ChartDTO {

    @NotNull
    String sensorStationId;

    @NotNull
    String sensorDataTypeInfoId;

    public String getSensorStationId() {
        return sensorStationId;
    }

    public void setSensorStationId(String sensorStationId) {
        this.sensorStationId = sensorStationId;
    }

    public String getSensorDataTypeInfoId() {
        return sensorDataTypeInfoId;
    }

    public void setSensorDataTypeInfoId(String sensorDataTypeInfoId) {
        this.sensorDataTypeInfoId = sensorDataTypeInfoId;
    }
}
