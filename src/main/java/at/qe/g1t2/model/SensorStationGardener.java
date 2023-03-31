package at.qe.g1t2.model;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class SensorStationGardener implements Persistable<String>, Serializable, Comparable<SensorStationGardener> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    private Userx gardener;
    @ManyToOne(optional = false)
    private SensorStation sensorStation;


    @Override
    public int compareTo(SensorStationGardener o) {
        return id.compareTo(Objects.requireNonNull(o.getId()));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Userx getGardener() {
        return gardener;
    }

    public void setGardener(Userx gardener) {
        this.gardener = gardener;
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorStationGardener that = (SensorStationGardener) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
