package at.qe.g1t2.RestAPI.model;

import jakarta.validation.constraints.NotNull;

public class SensorStationDTO {

    @NotNull
    private Long dipId;

    public Long getDipId() {
        return dipId;
    }

    public void setDipId(Long dipId) {
        this.dipId = dipId;
    }
}
