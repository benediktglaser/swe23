package at.qe.g1t2.model;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
public class SensorData implements Persistable<UUID>, Serializable, Comparable<SensorData> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    private double measurement;

    @ManyToOne(optional = false)
    private SensorStation sensorStation;

    private String type;

    private String unit;

    private LocalDateTime timestamp;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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

    public String getType() {
        return type;
    }

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

    public void setId(UUID id) {
        this.id = id;
    }

    public double getMeasurement() {
        return measurement;
    }

    public void setMeasurement(double value) {
        this.measurement = value;
    }


    @Override
    public int compareTo(SensorData o) {
        return Double.compare(o.measurement, this.measurement);
    }

    @Override
    public UUID getId() {
        return id;
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }

    @Override
    public boolean isNew() {
        return (null == createDate);
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorData that = (SensorData) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
