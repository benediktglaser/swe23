package at.qe.g1t2.restapi.model;

import jakarta.validation.constraints.NotNull;


public class SensorStationDTO {

    @NotNull
    private Long dipId;
    @NotNull
    private String mac;

    private Boolean verified;

    private Boolean connected;

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

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }
}
