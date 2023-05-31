import dbconnection as db
import sensordata


def test_inserting():
    path = "database_2.db"
    conn = db.access_database(path)
    db.create_tables(conn)

    db.init_limits(conn, 1, "A::CA")
    db.init_limits(conn, 2, "B::CA")
    db.insert_sensor_data(
        conn, sensordata.SensorData(1, 28.4, 1.0, 40.3, 23.1, 600, 87.345)
    )
    db.insert_sensor_data(
        conn, sensordata.SensorData(1, 1.4, 2.0, 23.3, 123.1, 700, 87.345)
    )
    db.insert_sensor_data(
        conn, sensordata.SensorData(2, 1.9, 21.0, 34.3, 123.1, 900, 187.345)
    )
    assert len(db.get_sensor_data(conn, 1)) == 2

    db.drop_sensor_data(conn)
    db.drop_limits(conn)
    conn.close()


def test_set_and_get_limit():
    path = "database_2.db"
    conn = db.access_database(path)
    db.create_tables(conn)

    db.init_limits(conn, 1, "B::CA")
    db.set_limits(conn, 1, "temp", -4.2, 6.8)
    assert db.get_limits(conn, 1, "temp")[0] == -4.2
    assert db.get_limits(conn, 1, "temp")[1] == 6.8
    db.drop_sensor_data(conn)
    db.drop_limits(conn)
    conn.close()


def test_remove_sensor_data():
    path = "database_2.db"
    conn = db.access_database(path)
    db.create_tables(conn)
    db.init_limits(conn, 1, "B::CA")
    db.init_limits(conn, 2, "AF::CA")

    db.insert_sensor_data(
        conn, sensordata.SensorData(1, 28.4, 1.0, 40.3, 23.1, 600, 87.345)
    )
    db.insert_sensor_data(
        conn, sensordata.SensorData(1, 1.4, 2.0, 23.3, 123.1, 700, 87.345)
    )
    db.insert_sensor_data(
        conn, sensordata.SensorData(2, 1.9, 21.0, 34.3, 123.1, 900, 187.345)
    )
    assert len(db.get_sensor_data(conn, 1)) == 2
    delete_data = db.get_sensor_data(conn, 1)[0]
    db.remove_sensor_data(conn, delete_data[0], delete_data[1])
    assert len(db.get_sensor_data(conn, 1)) == 1

    db.drop_sensor_data(conn)
    db.drop_limits(conn)
    conn.close()
    db.delete_database(path)
