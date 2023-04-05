import sqlite3
from datetime import datetime


def create_database(path):
    conn = sqlite3.connect(path)
    create_tables(conn)
    return conn


def create_tables(conn):
    cursor = conn.cursor()
    try:
        # TODO: find reasonable default values for limits
        cursor.execute("CREATE TABLE IF NOT EXISTS limits(station_id INTEGER, temp_lower REAL, temp_upper REAL,"
                       "pressure_lower REAL, pressure_upper REAL, humid_lower REAL, humid_upper REAL,"
                       "gas_lower REAL, gas_upper REAL, alt_lower REAL, alt_upper REAL,"
                       "soil_lower REAL, soil_upper REAL, light_lower REAL, light_upper REAL,"
                       "PRIMARY KEY(station_id))")
        # *_limit: relative excess regarding value and limit
        cursor.execute("CREATE TABLE IF NOT EXISTS sensor_data(station_id INTEGER, time_stamp TEXT,"
                       "temp REAL, temp_limit REAL, pressure REAL, pressure_limit REAL,"
                       "humid REAL, humid_limit REAL, gas REAL, gas_limit REAL,"
                       "alt REAL, alt_limit REAL, soil REAL, soil_limit REAL,"
                       "light REAL, light_limit REAL,"
                       "PRIMARY KEY(station_id, time_stamp));")
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)


def limit_calculator(value, lower_limit, upper_limit):
    # TODO: check what value is sent if sensor isn't picking anything up
    # if value == 0 or value is None:
    #     return 0

    if value >= lower_limit:
        if value <= upper_limit:
            return 0

        return abs(value - upper_limit) / upper_limit - lower_limit

    return abs(lower_limit - value) / upper_limit - lower_limit


def insert_sensor_data(conn, data):
    cursor = conn.cursor()
    try:
        cursor.execute("INSERT INTO sensor_data values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                       (data.station_id, datetime.now(), data.temperature, limit_calculator(data.temperature, 0, 10), data.pressure, 1,
                        data.humidity, 1, data.gas_resistance, 1, data.altitude, 1, data.soil, 1,
                        data.light, 1))
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)


def get_sensor_data(conn, station_id):
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT * "
                       "FROM sensor_data "
                       "WHERE station_id = ?",
                       (station_id,))
        record = cursor.fetchall()

        conn.commit()
        cursor.close()


        #convert record [(a, b), (c, d)] into a 2D-array [[a, b], [c, d]]
        result =[list(x) for x in record]

        return result

    except sqlite3.Error as e:
        print(e)

def remove_sensor_data(conn, station_id, time):
    cursor = conn.cursor()
    try:
        cursor.execute("DELETE FROM sensor_data "
                       "WHERE station_id = ? AND time_stamp = ?;",
                       (station_id, time))
        
        conn.commit()
        cursor.close()

    except sqlite3.Error as e:
        print(e)

