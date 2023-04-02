package at.qe.g1t2.RestAPI.model;


import jakarta.validation.constraints.NotNull;


public class AccessPointDTO {


    private String id;
    @NotNull
    private Double interval;
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getInterval() {
        return interval;
    }


    public void setInterval(Double interval) {
        this.interval = interval;
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

