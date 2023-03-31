package at.qe.g1t2.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class SensorDataTransfer {

    private Double measurement;

    private UUID sensorStation;

    private String type;

    private String unit;

    private LocalDateTime timestamp;


    public Double getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Double measurement) {
        this.measurement = measurement;
    }

    public UUID getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(UUID sensorStation) {
        this.sensorStation = sensorStation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
