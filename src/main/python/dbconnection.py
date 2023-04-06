import sqlite3
from datetime import datetime
import os


def create_database(path):
    conn = sqlite3.connect(path)
    create_tables(conn)
    return conn


def create_tables(conn):
    cursor = conn.cursor()
    try:
        cursor.execute("CREATE TABLE IF NOT EXISTS limits(station_id INTEGER,"
                       "temp_lower REAL, temp_upper REAL,"
                       "pressure_lower REAL, pressure_upper REAL,"
                       "quality_lower REAL, quality_upper REAL,"
                       "humid_lower REAL, humid_upper REAL,"
                       "soil_lower REAL, soil_upper REAL,"
                       "light_lower REAL, light_upper REAL,"
                       "PRIMARY KEY(station_id))")
        # *_limit: relative excess regarding value and limit
        cursor.execute("CREATE TABLE IF NOT EXISTS sensor_data(station_id INTEGER, time_stamp TEXT,"
                       "temp REAL, temp_limit REAL,"
                       "pressure REAL, pressure_limit REAL,"
                       "quality REAL, quality_limit REAL,"
                       "humid REAL, humid_limit REAL,"
                       "soil REAL, soil_limit REAL,"
                       "light REAL, light_limit REAL,"
                       "PRIMARY KEY(station_id, time_stamp));")
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)


def init_limits(conn, station_id):
    cursor = conn.cursor()
    try:
        # TODO: find reasonable default values for limits
        cursor.execute("INSERT INTO limits values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                       (station_id,
                        0, 1,  # temp
                        0, 1,  # pressure
                        0, 1,  # quality
                        0, 1,  # humid
                        0, 1,  # soil
                        0, 1))  # light
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)


def calculate_limit(value, lower_limit, upper_limit):
    # TODO: check what value is sent if sensor isn't picking anything up
    # if value == 0 or value is None:
    #     return 0

    if value >= lower_limit:
        if value <= upper_limit:
            return 0

        return abs(value - upper_limit) / upper_limit - lower_limit

    return abs(lower_limit - value) / upper_limit - lower_limit


def insert_sensor_data(conn, data):
    limits = get_limits(conn, data.station_id)
    # lower index = lower_limit, higher index = upper_limit
    temp_limits = limits[0:2]
    pressure_limits = limits[2:4]
    quality_limits = limits[4:6]
    humid_limits = limits[6:8]
    soil_limits = limits[8:12]
    light_limits = limits[10:12]

    cursor = conn.cursor()
    try:
        cursor.execute("INSERT INTO sensor_data values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                       (data.station_id, datetime.now(),
                        data.temperature, calculate_limit(data.temperature, temp_limits[0], temp_limits[1]),
                        data.pressure, calculate_limit(data.pressure, pressure_limits[0], pressure_limits[1]),
                        data.quality, calculate_limit(data.quality, quality_limits[0], quality_limits[1]),
                        data.humidity, calculate_limit(data.humidity, humid_limits[0], humid_limits[1]),
                        data.soil, calculate_limit(data.soil, soil_limits[0], soil_limits[1]),
                        data.light, calculate_limit(data.light, light_limits[0], light_limits[1])))
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)


def set_limits(conn, station_id, type, lower_limit, upper_limit):
    cursor = conn.cursor()
    try:
        match type:
            case 'temp':
                cursor.execute(("UPDATE limits "
                                "SET temp_lower = ?, "
                                "    temp_upper = ? "
                                "WHERE station_id = ?"),
                               (lower_limit, upper_limit, station_id))

            case 'pressure':
                cursor.execute(("UPDATE limits "
                                "SET pressure_lower = ?, "
                                "    pressure_upper = ? "
                                "WHERE station_id = ?"),
                               (lower_limit, upper_limit, station_id))

            case 'quality':
                cursor.execute(("UPDATE limits "
                                "SET quality_lower = ?, "
                                "    quality_upper = ? "
                                "WHERE station_id = ?"),
                               (lower_limit, upper_limit, station_id))

            case 'humid':
                cursor.execute(("UPDATE limits "
                                "SET humid_lower = ?, "
                                "    humid_upper = ? "
                                "WHERE station_id = ?"),
                               (lower_limit, upper_limit, station_id))

            case 'soil':
                cursor.execute(("UPDATE limits "
                                "SET soil_lower = ?, "
                                "    soil_upper = ? "
                                "WHERE station_id = ?"),
                               (lower_limit, upper_limit, station_id))

            case 'light':
                cursor.execute(("UPDATE limits "
                                "SET light_lower = ?, "
                                "    light_upper = ? "
                                "WHERE station_id = ?"),
                               (lower_limit, upper_limit, station_id))
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)


def get_limits(conn, station_id):
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT * "
                       "FROM limits "
                       "WHERE station_id = ?",
                       (station_id,))
        record = cursor.fetchone()
        # convert the record (tupel) into a list
        result = []
        for value in record:
            result.append(value)

        return result[1:]  # remove the first element (station_id)

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
        # convert record [(a, b), (c, d)] into a 2D-array [[a, b], [c, d]]
        result = [list(x) for x in record]
        return result


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


""" DEBUG ONLY """

def drop_sensor_data(conn):
    cursor = conn.cursor()
    try:
        cursor.execute("DROP TABLE IF EXISTS sensor_data;")
        conn.commit()
        cursor.close()

    except sqlite3.Error as e:
        print(e)


def delete_database(path):
    os.remove(path)
