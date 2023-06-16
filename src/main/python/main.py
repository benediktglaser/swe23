import configparser
import time
import restcontroller as rest
import logger
from threading import Thread, Event
import threading
import credentials
import dbconnection as db
import restcontroller_init as rci
import myble
import sys

lock = threading.Lock()
interval = 20


def init() -> dict:
    """
    This function initializes the database,
    checks the credentials, asks if the
    access_point is enabled.
    If the access_point has no credentials
    or has some which are not known
    to the Webserver it will register
    a new one

    Returns
    ---------
    dict:
        auth_head, DB-Path, Webserver-Address
    """

    # read the conf.yaml file
    config = configparser.ConfigParser()
    config.read_file(open(r"conf.yaml"))
    address = config.get("config", "address")
    global interval
    interval = int(config.get("config", "interval"))
    logger.log_info("start interval: " + str(interval))
    name = config.get("config", "name")

    # establish the database-connection
    path = "database.db"
    conn = db.access_database(path)
    db.create_tables(conn)

    # Read name and password from identification.yaml
    try:
        login = credentials.read_from_yaml()

    except:
        sys.exit(
            "Error when reading identification.yaml. Please make sure that the file is in the same directory as this script"
        )

    # Check if access_point exists on Webserver, if not create a new one
    if login is not None and login[0] is not None:
        access_point_exists = None
        while access_point_exists is None:
            access_point_exists = rci.request_if_access_point_exists(address, login[0])
            time.sleep(5)
        if not access_point_exists:
            logger.log_info("Credentials not recognized, register as new access_point")
            login = None

    # Register new access_point if necessary
    if login is None or login[0] is None or login[1] is None:
        logger.log_info("Register as new access_point")
        login = register_access_point(address, interval, name)
    auth_header = rci.prepare_auth_headers(login[0], login[1])

    # This loop checks for approval from the Webserver
    while True:
        enabled = rci.request_approval(address, auth_header)
        if enabled is True:
            logger.log_info("Successfully authenticated")
            break
        logger.log_info("waiting for authentication")
        time.sleep(10)

    logger.log_info("Login as: " + login[0] + " " + login[1])
    auth_header = rci.prepare_auth_headers(login[0], login[1])
    argument_list = {
        "authentication_header": auth_header,
        "address": address,
        "db_path": path,
        "name": login[0],
    }
    return argument_list


def main(args: dict):
    """
    This function starts all the
    threads.

    Arguments
    ---------
    args: a dictionary containing:
        auth_header: str,
        address: str,
        db_path: str
    """

    event = Event()
    ble_thread = Thread(
        target=myble.start_ble,
        args=(args["db_path"], args["address"], args["authentication_header"], event),
    )

    polling_for_interval_thread = Thread(
        target=poll_interval,
        args=(args["address"], args["authentication_header"], event),
    )

    polling_for_limits_thread = Thread(
        target=poll_limits,
        args=(args["address"], args["authentication_header"], event),
    )
    sending_sensor_data_thread = Thread(
        target=send_sensor_data,
        args=(args["address"], args["authentication_header"], event),
    )

    poll_if_access_point_still_exists_thread = Thread(
        target=poll_access_point_exists,
        args=(
            args["address"],
            args["name"],
        ),
    )

    ble_thread.start()
    polling_for_interval_thread.start()
    polling_for_limits_thread.start()
    sending_sensor_data_thread.start()
    poll_if_access_point_still_exists_thread.start()
    poll_if_access_point_still_exists_thread.join()

    # Preparing clean shutdown
    event.set()
    polling_for_interval_thread.join()
    polling_for_limits_thread.join()
    sending_sensor_data_thread.join()
    ble_thread.join()
    db.delete_database(args["db_path"])

    logger.log_info("Shutting down")
    logger.log_info("Shutdown complete")
    return


def poll_interval(address: str, auth_header: str, event: Event):
    """
    This function polls and checks
    if the interval has changed
    Arguments
    ---------
    address: str
        Address of the Webserver
    auth_header: str
        the auth_header for the rest-connection
    event: threading.Event
        Event that access_point was deleted on Webserver
    ---------
    Returns None
    """

    lock.acquire()
    try:
        global interval
        current_interval = interval

    except Exception as e:
        logger.log_error(e)

    finally:
        lock.release()

    while True:
        new_interval = rest.request_interval(address, auth_header)
        if new_interval is not None and new_interval is not current_interval:
            current_interval = new_interval
            lock.acquire()
            try:
                interval = current_interval
                logger.log_info("New Interval = " + str(current_interval))

            except Exception as e:
                logger.log_error(e)

            finally:
                lock.release()

        if event.is_set():
            break
        time.sleep(30)
    logger.log_info("Request interval thread quit")
    return


def poll_limits(address: str, auth_header: str, event: Event):
    """
    This function polls to check
    if the limits have changed.

    Arguments
    ---------
    address: str
        Address of the Webserver
    auth_header: str
        the auth_header for the rest-connection
    event: threading.Event
        Event that access_point was deleted on Webserver
    ---------
    Returns None
    """

    path = "database.db"
    conn = db.access_database(path)

    while True:
        list_of_sensor_stations = db.get_all_sensor_stations(conn)
        for (sensor_station_id, mac) in list_of_sensor_stations:
            new_limits = rest.request_limits(
                address, auth_header, int(sensor_station_id)
            )

            if new_limits is None:
                continue
            for list in new_limits:
                type_limit = list["dataType"]
                min_limit = list["minLimit"]
                max_limit = list["maxLimit"]

                db.update_limits(
                    conn, int(sensor_station_id), type_limit, min_limit, max_limit
                )
        if event.is_set():
            break
        time.sleep(60)
    logger.log_info("Limit thread quit")
    return


def send_sensor_data(address: str, auth_header: str, event: Event):
    """
    This function sends sensor_data
    and then deletes them from the database.

    Arguments
    ---------
    address: str
        Address of the Webserver
    auth_header: str
        the auth_header for the rest-connection
    event: threading.Event
        Event that access_point was deleted on Webserver
    ---------
    Returns None
    """

    path = "database.db"
    conn = db.access_database(path)

    while True:
        list_of_sensor_stations = db.get_all_sensor_stations(conn)
        for (sensor_station_id, _) in list_of_sensor_stations:

            list = db.get_sensor_data(conn, sensor_station_id)
            if len(list) != 0:
                sensor_data_delete = rest.post_measurement(address, list, auth_header)
                logger.log_info(
                    "delete this amount of data from database "
                    + "for sensor_station_id:"
                    + str(sensor_station_id)
                    + " :"
                    + str(len(sensor_data_delete))
                )
                rest.delete_send_sensor_data(conn, sensor_data_delete)
            else:
                logger.log_info(
                    "Found no records to send for station with id "
                    + str(sensor_station_id)
                )

        lock.acquire()
        try:
            my_interval = interval
            logger.log_info("interval in send_data: " + str(my_interval))

        except Exception as e:
            logger.log_error(
                "Reading global interval in sending_data failed: " + str(e)
            )
        finally:
            lock.release()
        if event.is_set():
            break
        time.sleep(my_interval)
    logger.log_info("Send data thread quit")
    return 


def poll_access_point_exists(address: str, name: str) -> bool:
    """
    This function checks if the access_point still exists
    on the Webserver.

    Arguments
    ---------
    address: str
        Address of the Webserver
    name: str
        name of the access_point
    ---------
    Returns bool
    """
    while True:
        exists = rci.request_if_access_point_exists(address, name)
        if not exists:
            logger.log_error("access_point has been deleted")
            break
        time.sleep(30)
    logger.log_info("Deletion of access_point has been noted")
    return True


def register_access_point(address: str, interval: int, name: str):
    """
    Register a new access_point.

    Arguments
    ---------
    address: str
        Address of the Webserver
    interval: int
        Interval in which registration is attempted.
    name: str
        Name of the access_point
    ---------
    Returns None
    """

    login = None
    while login is None or login[0] is None or login[1] is None:
        try:
            login = rci.register_access_point_at_server(address, interval, name)
            credentials.write_to_yaml(login[0], login[1])
        except:
            logger.log_info("No connection possible, trying again in 5sec...")
            logger.log_info(
                "To change the credentials, modify the identification.yaml file and restart the program"
            )
            time.sleep(15)
    return login


if __name__ == "__main__":
    arguments = init()
    main(arguments)
