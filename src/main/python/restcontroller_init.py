import requests
import logger

from requests.auth import HTTPBasicAuth


def prepare_auth_headers(my_id: str, my_password: str):
    """Just for testing. Prepare the authentication via HTTPBasic"""
    return HTTPBasicAuth(my_id, my_password)


def register_access_point_at_server(address: str, interval: float, name: str):
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
    try:
        resp = requests.get(f"{address}/api/accessPoint/enabled", auth=auth_header)
        if resp.status_code != 200:
            return None
        else:
            return resp.json()

    except Exception as e:
        print(e)
        logger.log_error(e)
        return None


def request_couple_mode(address: str, auth_header):
    try:
        resp = requests.get(f"{address}/api/accessPoint/couple", auth=auth_header)
        if resp.status_code != 200:
            return None
        else:
            return resp.json()

    except Exception as e:
        print(e)
        logger.log_error(e)
        return None
    

def request_sensorstation_if_verified(address:str, dipId:int, auth_header:str):
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



if __name__ == "__main__":
    pass
    # normally these two values come from the config-file
    # address = "http://localhost:8080"
    # data = register_access_point_at_server(address, 40.9, "Kappl")
    # my_id = data[0]
    # my_password = data[1]
    # auth = prepare_auth_headers(my_id, my_password)
    # print(request_approval(address, auth))
    # print(register_new_sensorstation_at_server(address, 93, auth))
