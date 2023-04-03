import dbconnection as db
import sensordata


def main():
    conn = db.create_database("database.db")
    db.insert_sensor_data(conn, sensordata.SensorData(1, 28.4, 1.0, 40.3, 23.1, 600, 87.345, 12.54))


if __name__ == '__main__':
    main()
