from dataclasses import dataclass


@dataclass
class SensorData:
    """
    A dataclass that includes all data collected by the sensorstation.
    station_id is the dip_id of the station.
    """

    station_id: int
    temperature: float
    pressure: float
    quality: float
    humidity: float
    soil: float
    light: float

    def set_value(self, data_type: str, value: float):
        if data_type == "temp":
            self.temperature = value
        elif data_type == "pressure":
            self.pressure = value
        elif data_type == "quality":
            self.quality = value
        elif data_type == "humid":
            self.humidity = value
        elif data_type == "soil":
            self.soil = value
        elif data_type == "light":
            self.light = value
