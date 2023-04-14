package at.qe.g1t2.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Audited
public class SensorDataTypeInfo implements Persistable<String>, Serializable, Comparable<SensorDataTypeInfo> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private SensorDataType type;

    private Double minLimit;
    private Double maxLimit;


    @ManyToOne(optional = false)
    private SensorStation sensorStation;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

    public void setId(String id) {
        this.id = id;
    }

    public SensorDataType getType() {
        return type;
    }

    public void setType(SensorDataType type) {
        this.type = type;
    }

    public Double getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(Double minLimit) {
        this.minLimit = minLimit;
    }

    public Double getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(Double maxLimit) {
        this.maxLimit = maxLimit;
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
        if (!(o instanceof SensorDataTypeInfo)) return false;
        SensorDataTypeInfo that = (SensorDataTypeInfo) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(SensorDataTypeInfo o) {
        return String.valueOf(o.getId()).compareTo(this.id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return null == createDate;
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }

    @Override
    public String toString() {
        return "Min Limit: " + minLimit + " Max Limit" + maxLimit + " Date: " + createDate;
    }
}
