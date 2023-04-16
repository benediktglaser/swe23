package at.qe.g1t2.restapi.model;

import jakarta.validation.constraints.NotNull;

public class SensorStationDTO {

    @NotNull
    private Long dipId;
    @NotNull
    private String mac;

    public Long getDipId() {
        return dipId;
    }

    public void setDipId(Long dipId) {
        this.dipId = dipId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
