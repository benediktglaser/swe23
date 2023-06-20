import sqlite3

import logger
import requests
import dbconnection


def post_measurement(address: str, list_of_measurements: list, auth_header: str):
    """
    Post the given measurement.
    Arguments
    ---------
    address : str
        The ip-address of the Server
    list_of_measurements: list
        The measurements in the order station_id : int, temperature : float, pressure : float
        quality : float, humidity : float, soil : float, light : float.
    auth_header : str
        The auth_header for the rest-connection
    Returns
    -------
    The inserted dictionary
    """

    list_of_responses = []
    measurements = prepare_for_jsf(list_of_measurements)

    # possible to delete data entries after sending
    for measurement in measurements:
        try:
            resp = requests.post(
                f"{address}/api/sensorData", json=measurement, auth=auth_header
            )
            if resp.__bool__:
                list_of_responses.append(
                    (
                        measurement.get("dipId"),
                        adjust_timestamp_for_database_access(
                            (measurement.get("timestamp"))
                        ),
                    ),
                )

        except Exception as e:
            logger.log_error(e)

    return list_of_responses


def adjust_timestamp_for_transfer(data: list[str]) -> list[str]:
    """
    This function takes the list from the database
    and convert the data into a String, which is readable
    for jsf-transmission.
    Arguments
    ---------
    data : list[str]
       The data
    Returns
    -------
    data : list[str]
    """

    for list_data in data:
        list_data[1] = list_data[1].replace(" ", "T")
    return data


def adjust_timestamp_for_database_access(date_string: str) -> str:
    """
    This function converts the date from the jsf-transmission
    back to a python-readable representation
    Arguments
    ---------
    date_string : str
        The date
    Returns
    -------
    date_string : str
        The date in the new format
    """

    date_string = date_string.replace("T", " ")
    return date_string


def prepare_for_jsf(data: list) -> list[dict]:
    """
    Prepare the data from the database to
    be sent via jsf by breaking it down
    into a dictionary
    Arguments
    ---------
    data : list
        The data as a list
    Returns
    -------
    data as dictionaries
    """

    data = adjust_timestamp_for_transfer(data)
    list_of_dicts = []
    for list_data in data:
        list_of_dicts.append(
            {
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "measurement": list_data[2],
                "type": "TEMPERATURE",
            }
        )
        list_of_dicts.append(
            {
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "measurement": list_data[4],
                "type": "PRESSURE",
            }
        )
        list_of_dicts.append(
            {
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "measurement": list_data[6],
                "type": "AIRQUALITY",
            }
        )
        list_of_dicts.append(
            {
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "measurement": list_data[8],
                "type": "HUMIDITY",
            }
        )
        list_of_dicts.append(
            {
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "measurement": list_data[10],
                "type": "SOIL",
            }
        )
        list_of_dicts.append(
            {
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "measurement": list_data[12],
                "type": "LIGHT",
            }
        )
    return list_of_dicts


def coupling_timed_out(address: str, auth_header: str):
    """
    Informs the Webserver that the coupling mode timed out.

    Arguments
    ---------
    address : str
        The ip-address of the Server
    auth_header : str
        The auth_header for the rest-connection
    """

    try:
        requests.get(f"{address}/api/accessPoint/couplingTimeout", auth=auth_header)
    except requests.exceptions.ConnectionError:
        logger.log_error(f"Unable to inform server about coupling timeout")


def refresh_connection_sensor_station(address: str, auth_header: str, dip_id: int):
    """
    Informs the Webserver that sensor_station is still reachable.

    address : str
        The ip-address of the Server
    auth_header : str
        The auth_header for the rest-connection
    dip_id : int
        The dip_id of the sensor_station
    """

    try:
        requests.get(f"{address}/api/sensorStation/refresh/{dip_id}", auth=auth_header)
    except requests.exceptions.ConnectionError:
        logger.log_error(f"Unable to refresh connection status of Station {dip_id}")


def delete_send_sensor_data(conn: sqlite3.Connection, list_of_tuples: list) -> None:
    """
    Remove a specific sensorData entry from the database
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    list_of_tuples:
        The data
    """

    for sensor_data in list_of_tuples:
        dbconnection.remove_sensor_data(conn, sensor_data[0], sensor_data[1])


def request_interval(address: str, auth_header: str):
    """
    Requests the interval from the Server
    Arguments
    ---------
    address : str
        The ip-address of the Server
    auth_header : str
        The auth_header for the rest-connection
    Returns
    -------
    response : JSON object
        The response of the rest-request
    or None
    """

    try:
        resp = requests.get(f"{address}/api/accessPoint/interval", auth=auth_header)
        if resp.status_code != 200:
            logger.log_error("Requesting interval failed: " + str(resp.status_code))
            return None
        else:
            return resp.json()

    except Exception as e:
        logger.log_error(e)
        return None


def request_limits(address: str, auth_header: str, dip_id: int):
    """
    Requests the limits from the Server
    Arguments
    ---------
    address : str
        The ip-address of the Server
    auth_header : str
        The auth_header for the rest-connection
    dip_id : int
        The dip_id of the sensor_station of which the limits should be changed
    Returns
    -------
    response : JSON object
        the response of the rest-request
    or None
    """

    try:
        resp = requests.get(
            f"{address}/api/sensorStation/limits/{dip_id}", auth=auth_header
        )
        if resp.status_code != 200:
            logger.log_error("Requesting limits failed: " + str(resp.status_code))
            return None
        else:
            return resp.json()

    except Exception as e:
        logger.log_error(e)
        return None


def request_sensor_station_status(address: str, auth_header: str, dip_id: int) -> dict[str, bool]:
    """
    Request if sensor_station is still enabled and
    if sensor_station is deleted
    Arguments
    ---------
    address : str
        The ip-address of the Server
    auth_header : str
        The auth_header for the rest-connection
    dip_id : int
        The dip_id of the sensor_station
    Returns
    -------
    response : dictionary with two booleans
       
    """

    try:
        resp = requests.get(
            f"{address}/api/sensorStation/enabled/{dip_id}", auth=auth_header
        )
        if resp.status_code != 200:
            logger.log_error(
                "Requesting if sensor_station is enabled failed: "
                + str(resp.status_code)
            )
            return {}
        else:
            return resp.json()

    except Exception as e:
        logger.log_error(e)
        return {}


def gardener_is_at_station(address: str, dip_id: int, auth_header: str) -> bool:
    """
    Method to inform webserver via REST, that a gardener is at the sensor_station.
    Arguments
    ---------
    address : str
        The ip-address of the Server
    dip_id: int
        dip_id of the sensor_station
    auth_header : str
        The auth_header for the rest-connection
    Returns
    -------
    response : boolean
        Whether the Message was successfully received.
        resp["enabled"] = true if the station is enabled, false otherwise
        resp["deleted"] = true if the station has been deleted, false otherwise
    
    """
    try:
        resp = requests.get(
            f"{address}/api/sensorStation/gardenerHere/{dip_id}", auth=auth_header
        )
        if resp.status_code != 200:
            logger.log_error(
                "Requesting if sensor_station is enabled failed: "
                + str(resp.status_code)
            )
            return False
        else:
            return True

    except Exception as e:
        logger.log_error(e)
        return None
