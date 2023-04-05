import requests
import dbconnection

from requests.auth import HTTPBasicAuth

host = "http://localhost:8080"


def prepare_auth_headers():
    return HTTPBasicAuth("elvis", "passwd")


def get_measurement(id_: int) -> dict:
    """
    Get the measurement with the given id.
    :param id_: the id of the measurement
    :return: dictionary representation of the measurement
    """
    auth_header = prepare_auth_headers()
    resp = requests.get(f"{host}/api/measurements/{id_}", auth=auth_header)
    # return the deserialized measurement object here
    return resp.json()


# accept measurement class instead of dict
def post_measurement_original_single(measurement: dict) -> dict:
    """
    Post the given measurement.
    :param measurement: The measurement to post as a dictionary
    :return: the inserted dictionary
    """
    auth_header = prepare_auth_headers()
    resp = requests.post(f"{host}/api/sensorData", json=measurement, auth=auth_header)
    # return the deserialized measurement object here
    return resp.json()


def post_measurement(measurements: list(dict)) -> dict:
    """
    Post the given measurement.
    :param measurement: The measurement to post as a dictionary
    :return: the inserted dictionary
    """
    auth_header = prepare_auth_headers()
    list_of_responses = []

    # possible to delete data entries after sending
    for measurement in measurements:
        resp = requests.post(
            f"{host}/api/sensorData", json=measurement, auth=auth_header
        )
        if resp.json() == "OK":
            pass
        list_of_responses.append(resp.json())

    # return the deserialized measurement object here
    return list_of_responses


def adjust_timestamp(data):
    for list_data in data:
        list_data[1] = list_data[1].replace(" ", "T")
    return data


def prepare_for_jsf(data):
    data = adjust_timestamp(data)
    list_of_dicts = []
    for list_data in data:
        list_of_dicts.append(
            {
                "accessPointName": "DUMMY",
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "temp": list_data[2],
                "temp_limit": list_data[3],
                "pressure": list_data[4],
                "pressure_limit": list_data[5],
                "humid": list_data[6],
                "humid_limit": list_data[7],
                "gas": list_data[8],
                "gas_limit": list_data[9],
                "alt": list_data[10],
                "alt_limit": list_data[11],
                "soil": list_data[12],
                "soil_limit": list_data[13],
                "light": list_data[14],
                "light_limit": list_data[15],
            }
        )

    print(list_of_dicts)
    return list_of_dicts


if __name__ == "__main__":
    conn = dbconnection.create_database("database.db")

    data = dbconnection.get_sensor_data(conn, 1)

    data = adjust_timestamp(data)

    print(data[0][1])
    print(data[0])

    prepare_for_jsf(data)
    # new_measurement = post_measurement_2(data[0])
    # new_measurement = post_measurement(
    {"measurement": 1.123, "accessPointName": "7269ddec-30c6-44d9-bc1f-8af18da09ed3", "dipId": 2, "unit": "Fahrenheit", "type": "TEST","timestamp": new_timestamp}
    # )
    # print(new_measurement)
