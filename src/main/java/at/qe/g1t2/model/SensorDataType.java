package at.qe.g1t2.model;

import java.io.Serializable;

public enum SensorDataType implements Serializable {
    TEMPERATURE("°C"),
    PRESSURE("Pa"),
    HUMIDITY("%"),
    SOIL("%"),
    LIGHT(""),
    AIRQUALITY("");

    private final String unit;

    SensorDataType(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }
}
