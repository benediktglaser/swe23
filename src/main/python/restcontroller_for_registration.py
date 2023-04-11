import requests

from requests.auth import HTTPBasicAuth


host = "http://localhost:8080"


global my_id
global my_password


def prepare_auth_headers():
    """Just for testing. Prepare the authentication via HTTPBasic"""
    return HTTPBasicAuth(my_id, my_password)


def register_access_point_at_server(interval: float, name: str):
    registration = {"accessPointName": name, "sendingInterval": interval}

    resp = requests.post(f"{host}/api/accessPoint/register", json=registration)
    # return the deserialized measurement object here
    return resp.json()


def register_new_sensorstation_at_server(dipId: int):
    auth_header = prepare_auth_headers()
    new_sensorstation = {"dipId": dipId}

    resp = requests.post(
        f"{host}/api/sensorStation/connect", json=new_sensorstation, auth=auth_header
    )
    # return the deserialized measurement object here
    return resp.json()


def request_new_limits(dipId: int):
    auth_header = prepare_auth_headers()
    data = {"dipId": str(dipId)}
    resp = requests.get(
        f"{host}/api/accessPoint/register/{dipId}", json=data, auth=auth_header
    )


if __name__ == "__main__":
    # normally these two values come from the config-file
    data = register_access_point_at_server(40.9, "Öztal")
    my_id = data["id"]
    my_password = data["password"]
    print(register_new_sensorstation_at_server(93))
    print(request_new_limits(93))
