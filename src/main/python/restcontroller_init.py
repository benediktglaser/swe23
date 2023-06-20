import requests
import logger

from requests.auth import HTTPBasicAuth


def prepare_auth_headers(my_id: str, my_password: str):
    """
    Prepare the authentication via HTTPBasic.
    Arguments
    ---------
    my_id : str
        The username for the rest-connection
    my_password : str
        The password for the rest-connection
    Returns
    -------
    HTTPBasicAuth:
        An instance of the HTTPBasicAuth Class
    """

    return HTTPBasicAuth(my_id, my_password)


def register_access_point_at_server(address: str, interval: float, name: str):
    """
    Register a new  at the Server
    Arguments
    ---------
    address : str
        The ip-address of the Server
    interval : str
        The interval in which the access_point sends data to the Server
    name : str
        The name of the sensor_station
    Returns
    -------
    (my_id, my_password) : (str, str)
    or None
    """

    try:
        registration = {"accessPointName": name, "sendingInterval": interval}

        resp = requests.post(f"{address}/api/accessPoint/register", json=registration)
        # return the deserialized measurement object here
        data = resp.json()
        if data is not None:
            my_id = data["id"]
            my_password = data["password"]
            return my_id, my_password
        else:
            return None

    except Exception as e:
        logger.log_error(e)


def propose_new_sensor_station_at_server(address: str, dip_id: int, mac: str, auth_header: str):
    """
    Register a new access_point at the Server
    Arguments
    ---------
    address: str
        The ip-address of the Server
    dip_id: int
        dip_id of the station
    mac: str
        MAC-Address of the station
    auth_header : str
        the auth_header for the rest-connection
    Returns
    -------
    requests.Response
    or None
    """

    new_sensor_station = {"dipId": dip_id, "mac": mac}

    try:
        resp = requests.post(
            f"{address}/api/sensorStation/register",
            json=new_sensor_station,
            auth=auth_header,
        )
        if resp.status_code != 200:
            logger.log_error(
                f"Error when proposing new sensor_station to Webserver: {resp.status_code}"
            )
            return None
        else:
            return resp.json()

    except Exception as e:
        logger.log_error(f"Error when proposing new sensor_station to Webserver: {resp.status_code}")
        return None


def request_approval(address: str, auth_header: str):
    """
    Asks the server whether the access_point is enabled.
    Arguments
    ---------
    address : str
        The ip-address of the Server
    auth_header : str
        the auth_header for the rest-connection
    Returns
    -------
    int,
    bool
    or None
    """

    try:
        resp = requests.get(f"{address}/api/accessPoint/enabled", auth=auth_header)

        if resp.status_code == 401:
            return 401
        if resp.status_code == 200:
            return True
        else:
            return False

    except Exception as e:
        logger.log_error(f"Error when requesting approval for accessPoint to Webserver: {e}")
        return None


def request_couple_mode(address: str, auth_header):
    """
    Asks the server if the access_point is in couple mode.
    Arguments
    ---------
    address : str
        The ip-address of the Server
    auth_header : str
        the auth_header for the rest-connection
    Returns
    -------
    response : JSON object
        the response of the rest-request
    or None
    """

    try:
        resp = requests.get(f"{address}/api/accessPoint/couple", auth=auth_header)
        if resp.status_code != 200:
            logger.log_error(f"Error when requesting couple_mode: {resp.status_code}")
            return None
        else:
            return resp.json()

    except Exception as e:
        logger.log_error(f"Exception when requesting couple_mode: {e}")
        return None
    

def request_sensor_station_if_verified(address: str, dip_id: int, auth_header: str):
    """
    Asks the server whether the connection to a certain sensor_station should be established.
    Arguments
    ---------
    address : str
        The ip-address of the Server
    dip_id : int
        The dip_id of the sensor_station for which we ask
    auth_header : str
        the auth_header for the rest-connection
    Returns
    -------
    response : JSON object
        the response of the rest-request
    or None
    """

    try:
        resp = requests.get(
            f"{address}/api/sensorStation/verified/{dip_id}", auth=auth_header
        )
        if resp.status_code != 200:
            logger.log_error("Error when requesting if sensor_station is enabled: " + str(resp.status_code))
            return None
        else:
            return resp.json()

    except Exception as e:
        logger.log_error(e)
        return None


def register_new_sensor_station_at_server(address: str, dip_id: int, auth_header: str):
    """
    Registers a new sensor_station at the server
    Arguments
    ---------
    address : str
        The ip-address of the Server
    dip_id : int
        The dip id of the sensor_station which we register
    auth_header : str
        the auth_header for the rest-connection
    Returns
    -------
    response : JSON object
        the response of the rest-request
    or None
    """

    try:
        resp = requests.get(
            f"{address}/api/sensorStation/connected/{dip_id}", auth=auth_header
        )
        if resp.status_code != 200:
            logger.log_error("Error when requesting if sensor_station is enabled: " + str(resp.status_code))
            return None
        else:
            return resp.json()

    except Exception as e:
        logger.log_error(e)
        return None


def connection_timed_out(address: str, dip_id: int, auth_header: str) -> None:
    """
    Informs the Webserver that establishing the initial connection to a sensor_station timed out.

    Arguments
    ---------
    address : str
        The ip-address of the Server
    dip_id : int
        The dip_id of the sensor_station for which we ask
    auth_header : str
        the auth_header for the rest-connection
    """

    try:
        requests.get(f"{address}/api/sensorStation/timeout/{dip_id}", auth=auth_header)
    except requests.exceptions.ConnectionError:
        logger.log_error(f"Unable to inform Webserver about connection timeout of Station {dip_id}")


def request_if_access_point_exists(address: str, name: str):
    """
    Checks if access_point exists at the webserver.
    Arguments
    ---------
    address : str
        The ip-address of the Server
    name : str
        Name of the access_point
    Returns
    -------
    response : JSON object
        the response of the rest-request:
        Boolean True -> access_point exists
        Boolean False -> access_point does not exist
    or None in case of error
    """

    try:
        resp = requests.get(
            f"{address}/api/accessPoint/register/credentials?accessPointId={name}"
        )
        if resp.status_code != 200:
            logger.log_error("Error when requesting if access_point does exist: " + str(resp.status_code))
            return None
        else:
            return resp.json()

    except Exception as e:
        logger.log_error(f"Couldn't request if access_point exists: {e}")
        return None
    



