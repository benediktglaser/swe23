package at.qe.g1t2.RestAPI.model;

import jakarta.validation.constraints.NotNull;

public class SensorStationDTO {

    private String id;
    @NotNull
    private Long dipId;
    @NotNull
    private String accessPointId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
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
