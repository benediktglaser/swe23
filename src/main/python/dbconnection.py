import os
import sqlite3
import sensordata
import logger
from datetime import datetime


def access_database(path):
    """
    Connects or Creates database and initializes both tables
    Arguments
    ---------
    path : str
        The path to the database-file
    Returns
    -------
    conn : sqlite3.Connection
        The connection to the database
    """

    conn = sqlite3.connect(path)
    return conn


def create_tables(conn):
    """
    Creates the limits and sensor_data table.
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    """

    cursor = conn.cursor()
    try:
        cursor.execute(
            "CREATE TABLE IF NOT EXISTS limits(station_id INTEGER, mac TEXT,"
            "temp_lower REAL, temp_upper REAL,"
            "pressure_lower REAL, pressure_upper REAL,"
            "quality_lower REAL, quality_upper REAL,"
            "humid_lower REAL, humid_upper REAL,"
            "soil_lower REAL, soil_upper REAL,"
            "light_lower REAL, light_upper REAL,"
            "PRIMARY KEY(station_id))"
        )
    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)

    try:
        # *_limit: relative excess regarding value and limit
        cursor.execute(
            "CREATE TABLE IF NOT EXISTS sensor_data(station_id INTEGER, time_stamp TEXT,"
            "temp REAL, temp_limit REAL,"
            "pressure REAL, pressure_limit REAL,"
            "quality REAL, quality_limit REAL,"
            "humid REAL, humid_limit REAL,"
            "soil REAL, soil_limit REAL,"
            "light REAL, light_limit REAL,"
            "PRIMARY KEY(station_id, time_stamp));"
        )
    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)

def init_limits(conn, station_id, mac):
    """
    Initialise the limits table with default values
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    station_id : int
        The station_id of the station of which the limits should be initialized
    mac : str
        MAC-Address of station
    """

    cursor = conn.cursor()
    try:
        cursor.execute(
            "INSERT INTO limits values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            (
                station_id,
                mac,
                0,
                1,  # temp
                0,
                1,  # pressure
                0,
                1,  # air-quality
                0,
                1,  # humid
                0,
                1,  # soil
                0,
                1,  # light
            ),
        )
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)


def calculate_limit(value, lower_limit, upper_limit):
    """
    Calculates the limit value to the given value. The limit states how much the upper_limit or
    lower_limit are exceeded or undercut in relation to the value. A limit value of 1 means the
    value is within the limits.
    Arguments
    ---------
    value : float
        The value of which the limit should be calculated
    lower_limit : float
        The lower limit of the value
    upper_limit : float
        The upper limit of the value
    Returns
    ---------
    limit : float
        By how much the upper_limit or lower_limit are exceeded or undercut
    """

    if value >= lower_limit:
        if value <= upper_limit:
            return 0

        return abs(value - upper_limit) / (upper_limit - lower_limit)

    return abs(lower_limit - value) / (upper_limit - lower_limit)


def insert_sensor_data(conn, data):
    """
    Inserts data into the sensor_data table. The station_id is located in the data-struct.
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    data : SensorData
    """

    limits = get_limits(conn, data.station_id, "all")
    if limits == []:
        logger.log_error(
            "Found no limits for station "
            + str(data.station_id)
            + " in insert_sensor_data"
        )
        return None
    # lower index = lower_limit, higher index = upper_limit
    temp_limits = limits[0:2]
    pressure_limits = limits[2:4]
    quality_limits = limits[4:6]
    humid_limits = limits[6:8]
    soil_limits = limits[8:12]
    light_limits = limits[10:12]

    date = datetime.now()

    cursor = conn.cursor()
    try:
        cursor.execute(
            "INSERT INTO sensor_data values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            (
                data.station_id,
                date,
                data.temperature,
                calculate_limit(data.temperature, temp_limits[0], temp_limits[1]),
                data.pressure,
                calculate_limit(data.pressure, pressure_limits[0], pressure_limits[1]),
                data.quality,
                calculate_limit(data.quality, quality_limits[0], quality_limits[1]),
                data.humidity,
                calculate_limit(data.humidity, humid_limits[0], humid_limits[1]),
                data.soil,
                calculate_limit(data.soil, soil_limits[0], soil_limits[1]),
                data.light,
                calculate_limit(data.light, light_limits[0], light_limits[1]),
            ),
        )
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)





def set_limits(conn, station_id, type, lower_limit, upper_limit):
    """
    Set new limits. Match was not used because of an older python-version
    running on the raspberry pi not supporting it.
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    station_id : int
        The (dip)id of the SensorStation
    type : str
        The type of data of which the limits should be changed.
    lower_limit : float
        The new lower limit
    upper_limit : float
        The new upper limit
    """

    cursor = conn.cursor()
    try:

        if type == "temp":
            cursor.execute(
                (
                    "UPDATE limits "
                    "SET temp_lower = ?, "
                    "   temp_upper = ? "
                    "WHERE station_id = ?"
                ),
                (lower_limit, upper_limit, station_id),
            )

        if type == "pressure":
            cursor.execute(
                (
                    "UPDATE limits "
                    "SET pressure_lower = ?, "
                    "    pressure_upper = ? "
                    "WHERE station_id = ?"
                ),
                (lower_limit, upper_limit, station_id),
            )

        if type == "quality":
            cursor.execute(
                (
                    "UPDATE limits "
                    "SET quality_lower = ?, "
                    "    quality_upper = ? "
                    "WHERE station_id = ?"
                ),
                (lower_limit, upper_limit, station_id),
            )

        if type == "humid":
            cursor.execute(
                (
                    "UPDATE limits "
                    "SET humid_lower = ?, "
                    "    humid_upper = ? "
                    "WHERE station_id = ?"
                ),
                (lower_limit, upper_limit, station_id),
            )

        if type == "soil":
            cursor.execute(
                (
                    "UPDATE limits "
                    "SET soil_lower = ?, "
                    "    soil_upper = ? "
                    "WHERE station_id = ?"
                ),
                (lower_limit, upper_limit, station_id),
            )

        if type == "light":
            cursor.execute(
                (
                    "UPDATE limits "
                    "SET light_lower = ?, "
                    "    light_upper = ? "
                    "WHERE station_id = ?"
                ),
                (lower_limit, upper_limit, station_id),
            )
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)


def get_limits(conn:str, station_id:int, type_limit: str):
    """
    Return a list of the limits if type_limit == ALL,
    otherwise a tuple (min, max) for the given type
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    station_id : int
        The (dip)id of the SensorStation
    type_limit : str
        The type of the limit
    Returns:
        a list of the limits if type_limit == ALL,
        a tuple (min, max) for the given type otherwise
    """

    cursor = conn.cursor()
    try:
        cursor.execute("SELECT * " "FROM limits " "WHERE station_id = ?", (station_id,))
        record = cursor.fetchone()
        # convert the record into a list
        result = []
        if record == None:
            logger.log_error(
                "Found no record for station with id "
                + str(station_id)
                + " in get_limits"
            )
            return []
        if record != None:
            for value in record:
                result.append(value)
        print(result)
        if type_limit == "temp":
            return (result[2], result[3])

        if type_limit == "pressure":
            return (result[4], result[5])

        if type_limit == "quality":
            return (result[6], result[7])

        if type_limit == "humid":
            return (result[8], result[9])

        if type_limit == "soil":
            return (result[10], result[11])

        if type_limit == "light":
            return (result[12], result[13])

        if type_limit == "all":
            return result[2:]

        logger.log_error("Couldn't find limit with name " + type_limit)
    except sqlite3.Error as e:
        logger.log_error(e)
        return []


def update_limits(
    conn:str, station_id: int, type_limit: str, lower_bound: float, upper_bound: float
):
    """
    Updates the limits of the given type
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    station_id : int
        The (dip)id of the SensorStation
    type_limit : str
        The type of the limit
    lower_bound: float
        The new lower limit
    lower_bound: float
        The new upper limit
    """

    cursor = conn.cursor()

    try:
        if type_limit == "TEMPERATURE":

            cursor.execute(
                "UPDATE limits SET temp_lower=?, temp_upper=? WHERE station_id = ?",
                (
                    lower_bound,
                    upper_bound,
                    station_id,
                ),
            )
        if type_limit == "PRESSURE":
            cursor.execute(
                "UPDATE limits SET pressure_lower=?, pressure_upper=? WHERE station_id = ?",
                (
                    lower_bound,
                    upper_bound,
                    station_id,
                ),
            )
        if type_limit == "HUMIDITY":
            cursor.execute(
                "UPDATE limits SET humid_lower=?, humid_upper=? WHERE station_id = ?",
                (
                    lower_bound,
                    upper_bound,
                    station_id,
                ),
            )
        if type_limit == "SOIL":
            cursor.execute(
                "UPDATE limits SET soil_lower=?, soil_upper=? WHERE station_id = ?",
                (
                    lower_bound,
                    upper_bound,
                    station_id,
                ),
            )
        if type_limit == "LIGHT":
            cursor.execute(
                "UPDATE limits SET light_lower=?, light_upper=? WHERE station_id = ?",
                (
                    lower_bound,
                    upper_bound,
                    station_id,
                ),
            )
        if type_limit == "AIRQUALITY":
            cursor.execute(
                "UPDATE limits SET quality_lower=?, quality_upper=? WHERE station_id = ?",
                (
                    lower_bound,
                    upper_bound,
                    station_id,
                ),
            )
        conn.commit()
        cursor.close()

    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)


def get_sensor_data(conn, station_id):
    """
    Retrieve sensor_data from database
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    station_id : int
        The (dip)id of the SensorStation
    Returns
    -------
    result : [SensorData]
    """

    cursor = conn.cursor()
    try:
        cursor.execute(
            "SELECT * " "FROM sensor_data " "WHERE station_id = ?", (station_id,)
        )
        record = cursor.fetchall()
        # convert record [(a, b), (c, d)] into a 2D-array [[a, b], [c, d]]
        result = [list(x) for x in record]
        return result
    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)


def remove_sensor_data(conn, station_id, time):
    """
    Remove sensor_data according to station_id and time
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    station_id : int
        The (dip)id of the SensorStation
    time : str
    """

    cursor = conn.cursor()
    try:
        cursor.execute(
            "DELETE FROM sensor_data " "WHERE station_id = ? AND time_stamp = ?;",
            (station_id, time),
        )
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)

def remove_sensorstation_from_limits (conn:str, station_id:int):
    """
    Remove sensorstation according to station_id from the limits table
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    station_id : int
        The (dip)id of the SensorStation
    """

    cursor = conn.cursor()
    try:
        cursor.execute(
            "DELETE FROM limits " "WHERE station_id = ?;",
            (str(station_id)),
        )
        conn.commit()
        cursor.close()
    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)





def get_all_sensorstations(conn:str):
    """
    Returns the station_id from all stations from the limits table.
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    Returns
    -------
    result : [int]
    """

    cursor = conn.cursor()
    try:
        cursor.execute("SELECT DISTINCT station_id, mac FROM limits;")
        record = cursor.fetchall()
        # convert record [(a, b), (c, d)] into a 2D-array [[a, b], [c, d]]
        result = [row for row in record]
        cursor.close()
        return result

    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)


def drop_sensor_data(conn):
    """
    DEBUG ONLY
    Deletes the entire sensor_data table
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    """

    cursor = conn.cursor()
    try:
        cursor.execute("DROP TABLE IF EXISTS sensor_data;")
        conn.commit()
        cursor.close()

    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)


def drop_limits(conn):
    """
    DEBUG ONLY
    Deletes the entire limits table
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    """

    cursor = conn.cursor()
    try:
        cursor.execute("DROP TABLE IF EXISTS limits;")
        conn.commit()
        cursor.close()

    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)


def delete_all_from_limits(conn: str):
    """
    DEBUG ONLY
    Deletes all entries from the limits table
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    """

    cursor = conn.cursor()
    try:
        cursor.execute("DELETE FROM limits;")
        conn.commit()
        cursor.close()

    except sqlite3.Error as e:
        print(e)
        logger.log_error(e)


def delete_database(path):
    """
    DEBUG ONLY
    Deletes the database file
    Arguments
    ---------
    path : str
        The path to the database-file
    """

    os.remove(path)


"""Just for testing"""
if __name__ == "__main__":
    host = "http://localhost:8080"
