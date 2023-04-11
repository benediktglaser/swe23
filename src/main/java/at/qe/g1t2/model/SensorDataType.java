package at.qe.g1t2.model;

public enum SensorDataType {
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
