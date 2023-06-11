package at.qe.g1t2.restapi.model;

import jakarta.validation.constraints.NotNull;

/**
 * This DTO is sent via REST to propose a new Sensorstation to the web server.
 */
public class SensorStationDTO {

    @NotNull
    private Long dipId;
    @NotNull
    private String mac;

    private Boolean verified;

    private Boolean connected;

    private boolean connectingTimeOut;

    public boolean isConnectingTimeOut() {
        return connectingTimeOut;
    }

    public void setConnectingTimeOut(boolean connectingTimeOut) {
        this.connectingTimeOut = connectingTimeOut;
    }

    public boolean getConnectingTimeOut() {
        return connectingTimeOut;
    }

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
