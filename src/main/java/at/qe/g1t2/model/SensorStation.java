package at.qe.g1t2.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
public class SensorStation implements Persistable<UUID>, Serializable, Comparable<SensorStation> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "sensorStation", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Set<Image> images = new HashSet<>();

    public Set<SensorData> getSensorData() {
        return sensorData;
    }

    public AccessPoint getAccessPoint() {
        return accessPoint;
    }

    public void setAccessPoint(AccessPoint accessPoint) {
        this.accessPoint = accessPoint;
    }

    public void setSensorData(Set<SensorData> sensorData) {
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
    private Set<SensorData> sensorData = new HashSet<>();

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

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }
}
