package at.qe.g1t2.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Audited
public class Picture implements Persistable<String>, Serializable, Comparable<Picture> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String pictureName;

    private String description;
    @Column(nullable = false)
    private String path;
    @Column(nullable = false)
    private LocalDateTime createDate;


    @ManyToOne(optional = false)
    private SensorStation sensorStation;

    @Override
    public int compareTo(Picture o) {
        return String.valueOf(o.id).compareTo(id);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return null == createDate;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }
}
