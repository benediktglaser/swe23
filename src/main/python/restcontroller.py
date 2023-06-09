import logger
from typing import List

import requests
from requests.auth import HTTPBasicAuth

import time

import dbconnection


def prepare_auth_headers(id: str, password: str):
    """DEPRECATED!"""
    return HTTPBasicAuth(id, password)


def post_measurement_original_single(
    address: str, measurement: dict, auth_header
) -> dict:
    """
    DEBUG ONLY.
    Post the given measurement.
    Arguments
    ---------
    address : str
        The ip-address of the Server
    measurement: dict
        The measurements. The key is the datatype, the data is the value
    auth_header : str
        The auth_header for the rest-connection
    Returns
    -------
    response : JSON object
        the response of the rest-request
    """

    # auth_header = prepare_auth_headers()
    resp = requests.post(
        f"{address}/api/sensorData", json=measurement, auth=auth_header
    )
    # return the deserialized measurement object here
    return resp.json()


def post_measurement(address: str, list_of_measurements: List, auth_header):
    """
    Post the given measurement.
    Arguments
    ---------
    address : str
        The ip-address of the Server
    list_of_measurements: List
        The measurements int he order station_id : int, temperature : float, pressure : float
    quality : float, humidity : float, soil : float, light : float.
    auth_header : str
        The auth_header for the rest-connection
    Returns
    -------
    The inserted dictionary
    """

    # auth_header = prepare_auth_headers()
    list_of_responses = []
    measurements = prepare_for_jsf(list_of_measurements)

    # possible to delete data entries after sending
    for measurement in measurements:
        try:
            resp = requests.post(
                f"{address}/api/sensorData", json=measurement, auth=auth_header
            )
            # print(resp.json())
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
            print(e)
            logger.log_error(e)

    # return the deserialized measurement object here
    return list_of_responses


def adjust_timestamp_for_transfer(data: str) -> str:
    """
    This function takes the list from the database
    and convert the data into a String, which is readable
    for jsf-transmission.
    Arguments
    ---------
    data : str
       The data
    Returns
    -------
    data : str
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


def prepare_for_jsf(data: List) -> List[dict]:
    """
    Prepare the data from the database to
    be sent via jsf by breaking it down
    into a dictionary
    Arguments
    ---------
    data : List
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
        requests.head(f"{address}/api/accessPoint/coupling", auth=auth_header)
    except requests.exceptions.ConnectionError:
        logger.log_error(f"Unable to inform server about coupling timeout")


def refresh_connection_sensor_station(address: str, auth_header: str, dipId: int):
    """
    Informs the Webserver that SensorStation is still reachable.

    address : str
        The ip-address of the Server
    auth_header : str
        The auth_header for the rest-connection
    dipId : int
        The dipId of the SensorStation
    """

    try:
        requests.head(f"{address}/api/sensorStation/refresh/{dipId}", auth_header)
    except requests.exceptions.ConnectionError:
        logger.log_error(f"Unable to refresh connection status of Station {dipId}")


def delete_send_sensor_data(conn, list_of_tuples) -> None:
    """
    Remove a specific sensorData entry from the database
    Arguments
    ---------
    conn : sqlite3.Connection
        The connection to the database
    list_of_tuples
        The data
    """

    for sensor_data in list_of_tuples:
        dbconnection.remove_sensor_data(conn, sensor_data[0], sensor_data[1])


def request_interval(address: str, auth_header):
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
            logger.log_error("Error when requesting interval: " + str(resp.status_code))
            return None
        else:
            return resp.json()

    except Exception as e:

        print(e)
        logger.log_error(e)
        return None


def request_limits(address: str, auth_header: str, dipId: int):
    """
    Requests the limits from the Server
    Arguments
    ---------
    address : str
        The ip-address of the Server
    auth_header : str
        The auth_header for the rest-connection
    dipId : int
        The dipId of the SensorStation of which the limits should be changed
    Returns
    -------
    response : JSON object
        the response of the rest-request
    or None
    """

    try:
        resp = requests.get(
            f"{address}/api/sensorStation/limits/{dipId}", auth=auth_header
        )
        if resp.status_code != 200:
            logger.log_error("Error when requesting interval: " + str(resp.status_code))
            return None
        else:
            return resp.json()

    except Exception as e:
        print(e)
        logger.log_error(e)
        return None


def request_sensorstation_status(address: str, auth_header: str, dip_id: int) -> dict[str, bool]:
    """
    Request if SensorStation is still enabled (true) and
    if SensorStation is deleted
    Arguments
    ---------
    address : str
        The ip-address of the Server
    auth_header : str
        The auth_header for the rest-connection
    dip_id : int
        The dipId of the SensorStation of which the limits should be changed
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
                "Error when requesting if sensorstation is enabled: "
                + str(resp.status_code)
            )
            return {}
        else:
            return resp.json()

    except Exception as e:
        print(e)
        logger.log_error(e)
        return {}


def gardener_is_at_station(address: str, dipId: int, auth_header: str) -> bool:
    """
    Method to inform webserver via REST, that a gardener is at the sensorstation.
    Arguments
    ---------
    address : str
        The ip-address of the Server
    dipId: int
        DipId of the sensorstation
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
            f"{address}/api/sensorStation/gardenerHere/{dipId}", auth=auth_header
        )
        if resp.status_code != 200:
            logger.log_error(
                "Error when requesting if sensorstation is enabled: "
                + str(resp.status_code)
            )
            return False
        else:
            return True

    except Exception as e:
        print(e)
        logger.log_error(e)
        return None


"""Just for testing"""
if __name__ == "__main__":
    host = "http://localhost:8080"
    conn = dbconnection.access_database("database.db")
    auth = prepare_auth_headers("43d5aba9-29c5-49b4-b4ec-2d430e34104f", "passwd")

    # dbconnection.insert_sensor_data(conn, sensordata.SensorData(3, 10, 2, 3, 4, 5, 17))

    # dbconnection.drop_sensor_data(conn)
    # dbconnection.drop_limits(conn)
    dbconnection.init_limits(conn, 1, "AC")
    dbconnection.init_limits(conn, 2, "BCC")
    while(True):
        response = request_if_is_sensorstation_enabled(host, auth, 1)
        print("enabled:", response["enabled"])
        print("deleted:", response["deleted"])
        time.sleep(30)

    dbconnection.drop_limits(conn)
    dbconnection.drop_sensor_data(conn)
