import dbconnection as db
import sensordata
import time


def main():
    path = "database.db"
    db.delete_database(path)
    conn = db.create_database(path)
    db.init_limits(conn, 1)
    db.init_limits(conn, 2)
    db.insert_sensor_data(conn, sensordata.SensorData(1, 28.4, 1.0, 40.3, 23.1, 600, 87.345))
    time.sleep(1)
    db.insert_sensor_data(conn, sensordata.SensorData(1, 28.4, 1.0, 40.3, 23.1, 600, 87.345))
    print(db.get_sensor_data(conn, 1))

    db.remove_sensor_data(conn, 1, db.get_sensor_data(conn, 1)[0][1])
    print(db.get_sensor_data(conn, 1))


if __name__ == '__main__':
    main()
