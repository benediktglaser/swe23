package at.qe.g1t2.restapi.model;

import jakarta.validation.constraints.NotNull;

public class SensorStationDTO {

    @NotNull
    private Long dipId;
    @NotNull
    private String MAC;

    public Long getDipId() {
        return dipId;
    }

    public void setDipId(Long dipId) {
        this.dipId = dipId;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }
}
