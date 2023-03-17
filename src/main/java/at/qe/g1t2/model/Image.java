package at.qe.g1t2.model;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Image {
    @Id
    private UUID id;

    private String name;

    @ManyToOne(optional = false)
    private SensorStation sensorStation;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String image;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
