import requests
import dbconnection
import sensordata

from requests.auth import HTTPBasicAuth

host = "http://localhost:8080"


def prepare_auth_headers():
    return HTTPBasicAuth("7269ddec-30c6-44d9-bc1f-8af18da09ed3", "passwd")


"""Only for testing not in the production"""


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


def post_measurement(measurements):
    """
    Post the given measurement.
    :param measurement: The measurement to post as a dictionary
    :return: the inserted dictionary
    """
    auth_header = prepare_auth_headers()
    list_of_responses = []

    # possible to delete data entries after sending
    for measurement in measurements:
        try:
            resp = requests.post(
                f"{host}/api/sensorData", json=measurement, auth=auth_header
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


def adjust_timestamp_for_transfer(data):
    """This function takes the list from the database
    and convert the data into a String, which is readable
    for jsf-transmission.
    :param list of lists
    :return list of lists
    """
    for list_data in data:
        list_data[1] = list_data[1].replace(" ", "T")
    return data


def adjust_timestamp_for_database_access(date_string):
    date_string = date_string.replace("T", " ")
    return date_string


def prepare_for_jsf(data):
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
                "type": "HUMIDITY",
            }
        )
        list_of_dicts.append(
            {
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "measurement": list_data[8],
                "type": "GAS",
            }
        )
        list_of_dicts.append(
            {
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "measurement": list_data[10],
                "type": "ALTITUDE",
            }
        )
        list_of_dicts.append(
            {
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "measurement": list_data[12],
                "type": "SOIL",
            }
        )
        list_of_dicts.append(
            {
                "dipId": list_data[0],
                "timestamp": list_data[1],
                "measurement": list_data[14],
                "type": "LIGHT",
            }
        )
    return list_of_dicts


def delete_send_sensor_data(conn, list_of_tuples):
    for sensor_data in list_of_tuples:
        dbconnection.remove_sensor_data(conn, sensor_data[0], sensor_data[1])


if __name__ == "__main__":
    conn = dbconnection.create_database("database.db")
    dbconnection.insert_sensor_data(conn, sensordata.SensorData(1, 1, 2, 3, 4, 5, 6, 7))
    # dbconnection.insert_sensor_data(
    # conn, sensordata.SensorData(1, 8, 9, 10, 11, 12, 13, 14)
    # )

    data = dbconnection.get_sensor_data(conn, 1)
    # dbconnection.drop_sensor_data(conn)

    data = adjust_timestamp_for_transfer(data)

    data = prepare_for_jsf(data)

    response = post_measurement(data)
    print(response)
    delete_send_sensor_data(conn, response)
    # new_measurement = post_measurement_2(data[0])
    """
    new_measurement = post_measurement_original_single(
        {
            "measurement": 6250,
            "dipId": 1,
            "type": "ALTITUDE",
            "timestamp": "1997-06-22T11:11:20",
        }
    )
    """
    # print(new_measurement)
