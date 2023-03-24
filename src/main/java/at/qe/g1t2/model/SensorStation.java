package at.qe.g1t2.model;

import at.qe.g1t2.services.SensorStationService;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class SensorStation implements Persistable<UUID>, Serializable, Comparable<SensorStation> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Boolean connected;

    private Boolean enabled;

    private Long dipId;


    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;

    private Double transmissionInterval;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "sensorStation", fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SensorStationGardener> sensorStationGardener = new ArrayList<>();

    public void setSensorStationGardener(List<SensorStationGardener> sensorStationGardener) {
        this.sensorStationGardener = sensorStationGardener;
    }

    public List<SensorStationGardener> getSensorStationGardener() {
        return sensorStationGardener;
    }

    @OneToMany(mappedBy = "sensorStation", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SensorData> sensorData = new ArrayList<>();

    public List<SensorData> getSensorData() {
        return sensorData;
    }

    public void setSensorData(List<SensorData> sensorData) {
        this.sensorData = sensorData;
    }

    public Double getTransmissionInterval() {
        return transmissionInterval;
    }

    public void setTransmissionInterval(Double transmissionInterval) {
        this.transmissionInterval = transmissionInterval;
    }


    public void setDipId(long dipId) {
        this.dipId = dipId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setDipId(Long dipId) {
        this.dipId = dipId;
    }

    public Long getDipId() {
        return dipId;
    }


    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }


    public AccessPoint getAccessPoint() {
        return accessPoint;
    }

    public void setAccessPoint(AccessPoint accessPoint) {
        this.accessPoint = accessPoint;
    }


    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }



    @ManyToOne()
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
