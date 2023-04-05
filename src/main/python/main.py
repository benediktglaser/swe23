import dbconnection as db
import sensordata


# global variable for accesspoint id


def main():
    conn = db.create_database("database.db")
    db.insert_sensor_data(conn, sensordata.SensorData(1, 1, 2, 3, 4, 5, 6, 7))
    db.insert_sensor_data(conn, sensordata.SensorData(1, 8, 9, 10, 11, 12, 13, 14))


if __name__ == "__main__":
    main()
