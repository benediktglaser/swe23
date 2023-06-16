package at.qe.g1t2.model;

import java.io.Serializable;

/**
 * This enum class represents the possibilities a SensorData can be marked as.
 */

public enum SensorDataType implements Serializable {
    TEMPERATURE("°C"),
    PRESSURE("hPa"),
    HUMIDITY("%"),
    SOIL(""),
    LIGHT(""),
    AIRQUALITY("kOhm");

    private final String unit;

    SensorDataType(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }
}
