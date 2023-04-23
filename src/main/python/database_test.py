import unittest

import dbconnection as db
import sensordata

unittest.TestLoader.sortTestMethodsUsing = None


class Test_database_insertion_and_retrieval(unittest.TestCase):
    def test_inserting(self):
        path = "database_2.db"
        conn = db.create_database(path)

        db.init_limits(conn, 1)
        db.init_limits(conn, 2)
        db.insert_sensor_data(
            conn, sensordata.SensorData(1, 28.4, 1.0, 40.3, 23.1, 600, 87.345)
        )
        db.insert_sensor_data(
            conn, sensordata.SensorData(1, 1.4, 2.0, 23.3, 123.1, 700, 87.345)
        )
        db.insert_sensor_data(
            conn, sensordata.SensorData(2, 1.9, 21.0, 34.3, 123.1, 900, 187.345)
        )
        self.assertEqual(
            len(db.get_sensor_data(conn, 1)), 2, "Incorrect amount of returned"
        )
        db.drop_sensor_data(conn)
        db.drop_limits(conn)
        conn.close()

    def test_set_and_get_limit(self):
        path = "database_2.db"
        conn = db.create_database(path)
        db.init_limits(conn, 1)
        db.set_limits(conn, 1, "temp", -4.2, 6.8)
        self.assertEqual(
            db.get_limits(conn, 1, "temp")[0], -4.2, "Incorrect lower_bound"
        )
        self.assertEqual(
            db.get_limits(conn, 1, "temp")[1], 6.8, "Incorrect upper_bound"
        )
        db.drop_sensor_data(conn)
        db.drop_limits(conn)
        conn.close()

    def remove_sensor_data(self):
        path = "database_2.db"
        conn = db.create_database(path)
        db.insert_sensor_data(
            conn, sensordata.SensorData(1, 28.4, 1.0, 40.3, 23.1, 600, 87.345)
        )
        db.insert_sensor_data(
            conn, sensordata.SensorData(1, 1.4, 2.0, 23.3, 123.1, 700, 87.345)
        )
        db.insert_sensor_data(
            conn, sensordata.SensorData(2, 1.9, 21.0, 34.3, 123.1, 900, 187.345)
        )
        delete_data = db.get_sensor_data(conn, 1)[0]
        print(delete_data)
        db.remove_sensor_data(conn, delete_data[0], delete_data[1])
        self.assertEqual(
            len(db.get_sensor_data(conn, 1)), 1, "Incorrect amount of returned"
        )
        db.drop_sensor_data(conn)
        db.drop_limits(conn)
        conn.close()


if __name__ == "__main__":
    unittest.main()
