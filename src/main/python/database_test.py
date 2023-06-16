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
    list_of_types = ["temp", ]
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


def test_calculate_limits_inside():
    result = db.calculate_limit(10, 8, 15)
    assert result == 1


def test_calculate_limits_under_limit():
    result = db.calculate_limit(5, 10, 20)
    assert result == 0.5


def test_calculate_limits_over_limit():
    result = db.calculate_limit(25, 10, 20)
    assert result == 0.5


def test_get_all_sensor_stations():
    path = "database_2.db"
    conn = db.access_database(path)
    db.create_tables(conn)
    db.init_limits(conn, 1, "B::CA")
    db.init_limits(conn, 2, "AF::CA")
    assert 2 == len(db.get_all_sensor_stations(conn))
    db.drop_sensor_data(conn)
    db.drop_limits(conn)
    conn.close()
    db.delete_database(path)


def test_update_limits():
    path = "database_2.db"
    conn = db.access_database(path)
    db.create_tables(conn)
    db.init_limits(conn, 1, "B::CA")
    db.init_limits(conn, 2, "AF::CA")       
    list_of_tuples = [("HUMIDITY", "humid"),("TEMPERATURE","temp"),("PRESSURE","pressure"), ("SOIL","soil"),("AIRQUALITY","quality" )]
    for tuple in list_of_tuples:
        db.update_limits(conn, 1, tuple[0], -4.0, 12)
        assert -4.0 == db.get_limits(conn, 1, tuple[1])[0]
        assert 12 == db.get_limits(conn, 1,tuple[1])[1]
    
    db.update_limits(conn, 1, "LIGHT", 33, 93)
    assert 33 == db.get_limits(conn, 1, "light")[0]
    assert 93 == db.get_limits(conn, 1,"light")[1]
    db.drop_sensor_data(conn)
    db.drop_limits(conn)
    conn.close()
    db.delete_database(path)


def test_delete_sensor_station():
    path = "database_2.db"
    conn = db.access_database(path)

    db.create_tables(conn)
    db.init_limits(conn, 1, "B::CA")
    db.init_limits(conn, 2, "AF::CA")
    db.remove_sensor_station_from_limits(conn, 2)
    assert 1 == len(db.get_all_sensor_stations(conn))

    db.remove_sensor_station_from_limits(conn,1 )
    assert 0 == len(db.get_all_sensor_stations(conn))

    db.drop_sensor_data(conn)
    db.drop_limits(conn)
    conn.close()
    db.delete_database(path)
