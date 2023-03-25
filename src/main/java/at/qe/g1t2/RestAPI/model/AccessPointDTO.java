package at.qe.g1t2.RestAPI.model;


import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;


public class AccessPointDTO {


    private String id;
    @NotNull
    private Double intervall;

    public Double getIntervall() {
        return intervall;
    }


    public void setIntervall(Double intervall) {
        this.intervall = intervall;
    }

    @NotNull
    private String accessPointName;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessPointName() {
        return accessPointName;
    }

    public void setAccessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
    }
}

