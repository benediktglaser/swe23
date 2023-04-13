import requests
import dbconnection
import sensordata
from typing import List

from requests.auth import HTTPBasicAuth

# host = "http://localhost:8080"


def prepare_auth_headers():
    """Just for testing. Prepare the authentication via HTTPBasic"""
    return HTTPBasicAuth("7269ddec-30c6-44d9-bc1f-8af18da09ed3", "passwd")


def post_measurement_original_single(
    address: str, measurement: dict, auth_header
) -> dict:
    """
    Only for testing not production!
    Post the given measurement.
    :param measurement: The measurement to post as a dictionary
    :return: the inserted dictionary
    """
    # auth_header = prepare_auth_headers()
    resp = requests.post(
        f"{address}/api/sensorData", json=measurement, auth=auth_header
    )
    # return the deserialized measurement object here
    return resp.json()


def post_measurement(address: str, list_of_measurements: List, auth_header) -> None:
    """
    Post the given measurement.
    :param measurement: The measurement to post as a dictionary
    :return: the inserted dictionary
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
            if resp.__bool__:
                list_of_responses.append(
                    (
                        measurement.get("dipId"),
                        adjust_timestamp_for_database_access(
                            (measurement.get("timestamp"))
                        ),
                    ),
                )

        except requests.exceptions.RequestException as e:
            print(e)

    # return the deserialized measurement object here
    return list_of_responses


def adjust_timestamp_for_transfer(data: str) -> str:
    """This function takes the list from the database
    and convert the data into a String, which is readable
    for jsf-transmission.
    :param list of lists
    :return list of lists
    """
    for list_data in data:
        list_data[1] = list_data[1].replace(" ", "T")
    return data


def adjust_timestamp_for_database_access(date_string: str) -> str:
    """This function converts the date from the jsf-transmission
    back to a python-readable representation"""
    date_string = date_string.replace("T", " ")
    return date_string


def prepare_for_jsf(data: List) -> List[dict]:
    """Prepare the data from the database to
    be sent via jsf by breaking it down
    into a dictionary"""
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


def delete_send_sensor_data(conn, list_of_tuples) -> None:
    """Remove a specific sensorData entry from the database"""
    for sensor_data in list_of_tuples:
        dbconnection.remove_sensor_data(conn, sensor_data[0], sensor_data[1])


def request_interval(address: str, auth_header):
    try:
        resp = requests.get(f"{address}/api/accessPoint/interval", auth=auth_header)
        if resp.status_code != 200:
            return None
        else:
            return resp.json()

    except Exception as e:
        print(e)
        return None
    

"""Just for testing"""
if __name__ == "__main__":
    host = "http://localhost:8080"
    conn = dbconnection.create_database("database.db")
    auth = prepare_auth_headers()
    dbconnection.init_limits(conn, 1)
    dbconnection.insert_sensor_data(conn, sensordata.SensorData(1, 1, 2, 3, 4, 5, 6))

    data = dbconnection.get_sensor_data(conn, 1)
    dbconnection.drop_sensor_data(conn)
    dbconnection.drop_limits(conn)

    #print(data)
    # data = adjust_timestamp_for_transfer(data)

    # data = prepare_for_jsf(data)

    # print(data)
    response = post_measurement(host, data, auth)
    print(response)
    # delete_send_sensor_data(conn, response)
