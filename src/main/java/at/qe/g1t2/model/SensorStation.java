package at.qe.g1t2.model;

import at.qe.g1t2.services.LogMsg;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This class represents the SensorStation Entity and has the purpose of structuring the connection between SensorStation
 * and AccessPoint. Therefore, contains a "many to one" relationship with AccessPoint. Additionally, this entity contains
 * a "many to many" relationship with users such that they are able to mark favourite SensorStations.
 */

@Entity
@Audited
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"access_point_id", "dipId"})})
public class SensorStation implements Persistable<String>, Serializable, Comparable<SensorStation> {


    @ManyToMany(mappedBy = "sensorStations",fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Fetch(FetchMode.SELECT)
    private Set<Userx> userx = new HashSet<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorStation.class);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Boolean connected;
    private Boolean enabled;
    @Column(nullable = false)
    private Long dipId;
    private String name;
    private String category;
    private LocalDateTime lastConnectedDate;
    @Column(unique = true)
    private String mac;
    @PreRemove
    protected void beforeRemove(){

        accessPoint.getSensorStation().remove(this);
    }
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
    @OneToMany(mappedBy = "sensorStation", fetch = FetchType.EAGER)
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

        logStatusChange("enabled",enabled,(LogMsg.LogType.OTHER));
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        logStatusChange("enabled",enabled,(LogMsg.LogType.OTHER));

        this.enabled = enabled;
    }

    public Long getDipId() {
        return dipId;
    }



    public void setDipId(Long dipId) {
        this.dipId = dipId;
    }

    public Boolean getConnected() {
        connected = lastConnectedDate != null && lastConnectedDate.plusSeconds((accessPoint.getThresholdInterval() ==null?0:accessPoint.getThresholdInterval().longValue()) + accessPoint.getSendingInterval().longValue()).isAfter(LocalDateTime.now());
        logStatusChange("connected",connected,(!connected ?LogMsg.LogType.CONNECTION_FAILURE:LogMsg.LogType.CONNECTED));
        return connected;
    }

    public void setConnected(Boolean connected) {

        this.connected = connected;
        logStatusChange("connected",connected,(!connected ?LogMsg.LogType.CONNECTION_FAILURE:LogMsg.LogType.CONNECTED));
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
        return this.id.compareTo(o.id);
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
    private void logStatusChange(String fieldName, Boolean fieldValue, LogMsg.LogType type) {
        LogMsg<String,SensorStation> msg = new LogMsg<>(type, SensorStation.class,"SensorStation: UUID" + id,fieldName+": " + fieldValue.toString(),null);
        if(fieldValue){
            LOGGER.info(msg.getMessage());
        }
        else{
            LOGGER.warn(msg.getMessage());
        }
    }
}
