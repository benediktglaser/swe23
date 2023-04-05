package at.qe.g1t2.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Audited
public class UsersFavourites implements Persistable<String>, Serializable, Comparable<UsersFavourites> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    Userx user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_station_id")
    SensorStation sensorStation;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return (null==createDate);
    }

    public void setId(String id) {
        this.id = id;
    }

    public Userx getUser() {
        return user;
    }

    public void setUser(Userx user) {
        this.user = user;
    }

    public SensorStation getSensorStation() {
        return sensorStation;
    }

    public void setSensorStation(SensorStation sensorStation) {
        this.sensorStation = sensorStation;
    }

    @Override
    public int compareTo(UsersFavourites o) {
        return 0;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersFavourites that = (UsersFavourites) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(sensorStation, that.sensorStation) && Objects.equals(createDate, that.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, sensorStation, createDate);
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }


}
