from dataclasses import dataclass


@dataclass
class SensorData:
    station_id: int
    temperature: float
    pressure: float
    humidity: float
    gas_resistance: float
    altitude: float
    soil: float
    light: float
