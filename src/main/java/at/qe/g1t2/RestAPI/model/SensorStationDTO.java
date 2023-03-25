package at.qe.g1t2.RestAPI.model;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class SensorStationDTO {

    private UUID id;
    @NotNull
    private Long dipId;
    @NotNull
    private String accessPointId;



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getDipId() {
        return dipId;
    }

    public void setDipId(Long dipId) {
        this.dipId = dipId;
    }

    public String getAccessPointId() {
        return accessPointId;
    }

    public void setAccessPointId(String accessPointId) {
        this.accessPointId = accessPointId;
    }
}
