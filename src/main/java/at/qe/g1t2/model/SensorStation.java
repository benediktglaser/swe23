package at.qe.g1t2.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Audited
public class SensorStation implements Persistable<String>, Serializable, Comparable<SensorStation> {

    @ManyToMany(mappedBy = "sensorStations",fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Fetch(FetchMode.SELECT)
    Set<Userx> userx = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Boolean connected;
    private Boolean enabled;
    private Long dipId;
    private String name;
    private String category;
    private LocalDateTime lastConnectedDate;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;
    @ManyToOne(optional = true)
    @JoinColumn(name = "gardener_id", referencedColumnName = "username")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Userx gardener;
    @OneToMany(mappedBy = "sensorStation", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SensorData> sensorData = new ArrayList<>();
    @OneToMany(mappedBy = "sensorStation", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SensorDataTypeInfo> sensorDataTypeInfos = new ArrayList<>();
    @OneToMany(mappedBy = "sensorStation", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Picture> pictures = new ArrayList<>();
    @ManyToOne()
    private AccessPoint accessPoint;

    public Set<Userx> getUserx() {
        return userx;
    }

    public void setUserx(Set<Userx> userx) {
        this.userx = userx;
    }

    public LocalDateTime getLastConnectedDate() {
        return lastConnectedDate;
    }

    public void setLastConnectedDate(LocalDateTime lastConnectedDate) {
        this.lastConnectedDate = lastConnectedDate;
    }

    public List<SensorData> getSensorData() {
        return sensorData;
    }

    public void setSensorData(List<SensorData> sensorData) {
        this.sensorData = sensorData;
    }


    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getDipId() {
        return dipId;
    }

    public void setDipId(long dipId) {
        this.dipId = dipId;
    }

    public void setDipId(Long dipId) {
        this.dipId = dipId;
    }

    public Boolean getConnected() {
        connected = lastConnectedDate != null && lastConnectedDate.plusSeconds((accessPoint.getThresholdInterval() ==null?0:accessPoint.getThresholdInterval().longValue()) + accessPoint.getSendingInterval().longValue()).isAfter(LocalDateTime.now());
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

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public Userx getGardener() {
        return gardener;
    }

    public void setGardener(Userx gardener) {
        this.gardener = gardener;
    }

    public List<SensorDataTypeInfo> getSensorDataTypeInfos() {
        return sensorDataTypeInfos;
    }

    public void setSensorDataTypeInfos(List<SensorDataTypeInfo> sensorDataTypeInfos) {
        this.sensorDataTypeInfos = sensorDataTypeInfos;
    }

}
