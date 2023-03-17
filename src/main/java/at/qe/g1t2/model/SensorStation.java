package at.qe.g1t2.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class SensorStation implements Persistable<UUID>, Serializable, Comparable<SensorStation> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public List<SensorData> getSensorData() {
        return sensorData;
    }

    public AccessPoint getAccessPoint() {
        return accessPoint;
    }

    public void setAccessPoint(AccessPoint accessPoint) {
        this.accessPoint = accessPoint;
    }

    public void setSensorData(List<SensorData> sensorData) {
        this.sensorData = sensorData;
    }

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @OneToMany(mappedBy = "sensorStation", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SensorData> sensorData = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REMOVE})
    @JoinColumn(nullable = true,name = "gardenerId", referencedColumnName = "username", insertable = false, updatable = true)
    private Userx gardener;

    public Userx getGardener() {
        return gardener;
    }

    public void setGardener(Userx gardener) {
        this.gardener = gardener;
    }

    @ManyToOne
    private AccessPoint accessPoint;

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int compareTo(SensorStation o) {
        return o.getName().compareTo(this.name);
    }

    @Override
    public boolean isNew() {
        return (null == createDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorStation that = (SensorStation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
