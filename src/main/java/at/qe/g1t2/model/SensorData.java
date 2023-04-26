package at.qe.g1t2.model;


import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Audited
public class SensorData implements Persistable<String>, Serializable, Comparable<SensorData> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private double measurement;

    @ManyToOne(optional = false)
    private SensorStation sensorStation;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SensorDataType type;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public SensorDataType getType() {
        return type;
    }

    public void setType(SensorDataType type) {
        this.type = type;
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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
