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

    private SensorType type;

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

    public SensorType getType() {
        return type;
    }

    public void setType(SensorType type) {
        this.type = type;
    }

    @Override
    public int compareTo(SensorData o) {
        return Double.compare(o.measurement,this.measurement);
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
        return Double.compare(that.measurement, measurement) == 0 && Objects.equals(id, that.id) && Objects.equals(sensorStation, that.sensorStation) && type == that.type && Objects.equals(createDate, that.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, measurement, sensorStation, type, createDate);
    }
}
