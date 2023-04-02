package at.qe.g1t2.RestAPI.model;


import jakarta.validation.constraints.NotNull;


public class AccessPointDTO {

    @NotNull
    private String accessPointName;
    @NotNull
    private Double sendingInterval;

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }

    public Double getSendingInterval() {
        return sendingInterval;
    }

    public void setSendingInterval(Double sendingInterval) {
        this.sendingInterval = sendingInterval;
    }
}

