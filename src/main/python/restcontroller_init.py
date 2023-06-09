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
    : HTTPBasicAuth
        An instance of the HTTPBasicAuth Class
    """

    return HTTPBasicAuth(my_id, my_password)


def register_access_point_at_server(address: str, interval: float, name: str):
    """
    Register a new AccessPoint at the Server
    Arguments
    ---------
    address : str
        The ip-address of the Server
    interval : str
        The interval in which the AccessPoint sends data to the Server
    name : str
        The name of the SensorStation
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
        if data != None:
            my_id = data["id"]
            my_password = data["password"]
            return (my_id, my_password)
        else:
            return None

    except Exception as e:
        print(e)
        logger.log_error(e)


def propose_new_sensorstation_at_server(
    address: str, dipId: int, mac: str, auth_header: str
):
    """
    Register a new AccessPoint at the Server
    Arguments
    ---------
    address : str
        The ip-address of the Server
    interval : str
        The interval in which the AccessPoint sends data to the Server
    name : str
        The name of the SensorStation
    Returns
    -------
    (my_id, my_password) : (str, str)
    or None
    """

    new_sensorstation = {"dipId": dipId, "mac": mac}

    try:
        resp = requests.post(
            f"{address}/api/sensorStation/register",
            json=new_sensorstation,
            auth=auth_header,
        )
        if resp.status_code != 200:
            logger.log_error(
                "Error when requesting if sensorstation is enabled: "
                + str(resp.status_code)
            )
            return None
        else:
            return resp.json()

    except Exception as e:
        print(e)
        logger.log_error(e)
        return None


def request_approval(address: str, auth_header):
    """
    Asks the server whether the AccessPoint is enabled.
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
        resp = requests.get(f"{address}/api/accessPoint/enabled", auth=auth_header)
        print(resp)

        if resp.status_code == 401:
            return 401
        if resp.status_code == 200:
            return True
        else:
            return False

    except Exception as e:
        print(e)
        logger.log_error(e)
        return None


def request_couple_mode(address: str, auth_header):
    """
    Asks the server if the AccessPoint is in couple mode.
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
        print(resp)
        if resp.status_code != 200:
            return None
        else:
            return resp.json()

    except Exception as e:
        print(e)
        logger.log_error(e)
        return None
    

def request_sensorstation_if_verified(address:str, dipId:int, auth_header:str):
    """
    Asks the server whether the connection to a certain SensorStation should be established.
    Arguments
    ---------
    address : str
        The ip-address of the Server
    dipId : int
        The dip id of the SensorStation for which we ask
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
            f"{address}/api/sensorStation/verified/{dipId}", auth=auth_header
        )
        if resp.status_code != 200:
            logger.log_error("Error when requesting if sensorstation is enabled: " + str(resp.status_code))
            return None
        else:
            return resp.json()

    except Exception as e:
        print(e)
        logger.log_error(e)
        return None


def register_new_sensorstation_at_server(address:str, dipId: int, auth_header:str):
    """
    Registers a new SensorStation at the server
    Arguments
    ---------
    address : str
        The ip-address of the Server
    dipId : int
        The dip id of the SensorStation for which we ask
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
            f"{address}/api/sensorStation/connected/{dipId}", auth=auth_header
        )
        if resp.status_code != 200:
            logger.log_error("Error when requesting if sensorstation is enabled: " + str(resp.status_code))
            return None
        else:
            return resp.json()

    except Exception as e:
        print(e)
        logger.log_error(e)
        return None


def connection_timed_out(address: str, dipId: int, auth_header: str):
    """
    Informs the Webserver that establishing the initial connection to a sensor_station timed out.

    Arguments
    ---------
    address : str
        The ip-address of the Server
    dipId : int
        The dip id of the SensorStation for which we ask
    auth_header : str
        the auth_header for the rest-connection
    """

    try:
        requests.head(f"{address}/api/accessPoint/timeout/{dipId}", auth=auth_header)
    except requests.exceptions.ConnectionError:
        logger.log_error(f"Unable to inform Webserver about connection timeout of Station {dipId}")


def request_if_accesspoint_exists(address:str, name:str):
    """
    Checks if a accesspoint exists at the webserver. 
    Arguments
    ---------
    address : str
        The ip-address of the Server
    name : str
        Name of the accesspoint
    Returns
    -------
    response : JSON object
        the response of the rest-request:
        Boolean True -> accespoint exists
        Boolean False -> accespoint does not exist
    or None in case of error
    """

    try:
        resp = requests.get(
            f"{address}/api/accessPoint/register/credentials?accessPointId={name}"
        )
        if resp.status_code != 200:
            logger.log_error("Error when requesting if accessPoint does exist: " + str(resp.status_code))
            return None
        else:
            return resp.json()

    except Exception as e:
        logger.log_error(f"Couldn't request if AccessPoint exists: {e}")
        return None




if __name__ == "__main__":
    #pass
    address = "http://localhost:8080"
    print(request_if_accesspoint_exists(address, "set"))
