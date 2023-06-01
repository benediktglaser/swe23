package at.qe.g1t2.restapi.model;

import java.io.Serializable;

public class SensorStationStatusDTO implements Serializable {

    private Boolean enabled;

    private Boolean deleted;


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
