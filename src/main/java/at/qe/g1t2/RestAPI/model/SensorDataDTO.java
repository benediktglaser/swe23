package at.qe.g1t2.RestAPI.model;

import at.qe.g1t2.model.SensorDataType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;


public class SensorDataDTO implements Serializable {


    @NotNull
    private double measurement;
    @NotNull
    private Long dipId;


    @Enumerated(EnumType.STRING)
    private SensorDataType type;

    private LocalDateTime timestamp;


    public Double getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Double measurement) {
        this.measurement = measurement;
    }

    public SensorDataType getType() {
        return type;
    }

    public void setType(SensorDataType type) {
        this.type = type;
    }

    public void setMeasurement(double measurement) {
        this.measurement = measurement;
    }

    public Long getDipId() {
        return dipId;
    }

    public void setDipId(Long dipId) {
        this.dipId = dipId;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
