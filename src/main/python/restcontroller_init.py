import requests

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


def register_new_sensorstation_at_server(address: str, dipId: int, auth_header):
    new_sensorstation = {"dipId": dipId}

    resp = requests.post(
        f"{address}/api/sensorStation/connect", json=new_sensorstation, auth=auth_header
    )
    # return the deserialized measurement object here
    return resp.json()


def request_limits(address: str, dipId: int, auth_header):
    try:
        data = {"dipId": str(dipId)}
        resp = requests.get(
            f"{address}/api/accessPoint/register/{dipId}", json=data, auth=auth_header
        )
        return resp.json()
    except Exception as e:
        print(e)


def request_approval(address: str, auth_header):
    try:
        resp = requests.get(f"{address}/api/accessPoint/enabled", auth=auth_header)
        if resp.status_code != 200:
            return None
        else:
            return resp.json()

    except Exception as e:
        print(e)
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
