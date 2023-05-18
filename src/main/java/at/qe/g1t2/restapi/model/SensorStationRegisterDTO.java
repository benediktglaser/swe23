package at.qe.g1t2.restapi.model;

/**
 * This DTO is shown on the webserver, to indicate that this sensorstation could be
 * connected to an accesspoint
 */
public class SensorStationRegisterDTO {

    private Boolean available;
    private Boolean alreadyConnected;

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getAlreadyConnected() {
        return alreadyConnected;
    }

    public void setAlreadyConnected(Boolean alreadyConnected) {
        this.alreadyConnected = alreadyConnected;
    }
}
