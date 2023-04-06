from dataclasses import dataclass


@dataclass
class SensorData:
    station_id: int
    temperature: float
    pressure: float
    quality: float
    humidity: float
    soil: float
    light: float
