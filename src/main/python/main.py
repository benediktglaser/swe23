import dbconnection as db
import configparser
import restcontroller_init as rci
import credentials
import time
import restcontroller as rest
import sensordata
import logger
from threading import Thread
import threading

lock = threading.Lock()
interval = 10


def init():
    # read the conf.yaml file
    config = configparser.ConfigParser()
    config.read_file(open(r"conf.yaml"))  # TODO: change on raspberry
    address = config.get("config", "address")
    global interval
    interval = int(config.get("config", "interval"))
    logger.log_debug("start interval: " + str(interval))
    name = config.get("config", "name")

    # establish the database-connection
    path = "database.db"  # TODO: change on raspberry
    conn = db.create_database(path)

    # establish a (first time) connection the the webserver
    login = credentials.read_from_yaml()

    while login is None or login[0] is None or login[1] is None:
        try:
            login = rci.register_access_point_at_server(address, interval, name)
            credentials.write_to_yaml(login[0], login[1])
        except:
            print("No connection possible, trying again in 5sec...")
            print(
                "To change the credentials, modify the identification.yaml file and restart the program"
            )
            time.sleep(15)
        auth_header = rci.prepare_auth_headers(login[0], login[1])

        # This is the loop
        while True:
            enabled = rci.request_approval(address, auth_header)
            if enabled:
                break
            print("waiting for authentication")
            time.sleep(10)

    print(login)
    logger.log_info("Login as: " + login[0] + " " + login[1])
    auth_header = rci.prepare_auth_headers(login[0], login[1])
    argument_list = {
        "authentication_header": auth_header,
        "address": address,
        # "interval": interval,
    }
    return argument_list


def poll_couple_mode(address: str, auth_header: str):
    i = 0
    while True:
        start_coupling = rci.request_couple_mode(address, auth_header)
        print("Coupling:", start_coupling)
        if start_coupling is True:
            # TODO call real coupling method
            logger.log_info("Starting coupling mode")
            dummy_couple_method()
            time.sleep(5)
        # DEBUG ONLY
        i = i + 1
        if i == 6:
            break


def dummy_couple_method():
    """DEBUG ONLY"""
    print("working in couple method")
    time.sleep(10)


def poll_interval(address: str, auth_header: str):
    lock.acquire()
    try:
        global interval
        current_interval = interval

    except Exception as e:
        logger.log_error(e)

    finally:
        lock.release()

    i = 0
    while True:
        new_interval = rest.request_interval(address, auth_header)
        print("interval:", current_interval)
        if new_interval is not None and new_interval is not current_interval:
            # TODO call real coupling method
            current_interval = new_interval
            lock.acquire()
            try:
                interval = current_interval
                logger.log_info("New Interval = " + str(current_interval))

            except Exception as e:
                logger.log_error(e)

            finally:
                lock.release()

        time.sleep(20)
        # TODO this method times out after certain
        i = i + 1
        if i == 3:
            break


def send_sensor_data(address: str, auth_header: str):
    i = 1
    path = "database.db"  # TODO: change on raspberry
    conn = db.create_database(path)

    db.init_limits(conn, 1)
    db.init_limits(conn, 2)
    while True:
        data_1 = sensordata.SensorData(1, 8 * i, 9 * i, 10 * i, 11 * i, 12 * i, 13 * i)
        data_2 = sensordata.SensorData(
            2, 18 * i, 19 * i, 110 * i, 111 * i, 112 * i, 113 * i
        )
        db.insert_sensor_data(conn, data_1)
        db.insert_sensor_data(conn, data_2)
        list_of_sensorstations = db.get_all_sensorstations(conn)
        for sensorstation_id in list_of_sensorstations:

            list = db.get_sensor_data(conn, sensorstation_id)
            sensor_data_delete = rest.post_measurement(address, list, auth_header)
            print("delete: ", sensor_data_delete)
            logger.log_info(
                "delete this amount of data from database "
                + "for sensorstation_id:"
                + str(sensorstation_id)
                + " :"
                + str(len(sensor_data_delete))
            )
            rest.delete_send_sensor_data(conn, sensor_data_delete)

        i = i + 1

        lock.acquire()
        try:
            my_interval = interval
            logger.log_debug("interval in send_data: " + str(my_interval))

        except Exception as e:
            logger.log_error("Reading global interval in sending_data failed: " + e)
        finally:
            lock.release()
        if i == 3:
            break
        time.sleep(my_interval)


if __name__ == "__main__":
    arguments = init()

    polling_for_couple_mode_thread = Thread(
        target=poll_couple_mode,
        args=(arguments["address"], arguments["authentication_header"]),
    )
    polling_for_interval_thread = Thread(
        target=poll_interval,
        args=(
            arguments["address"],
            arguments["authentication_header"],
        ),
    )
    sending_sensor_data_thread = Thread(
        target=send_sensor_data,
        args=(
            arguments["address"],
            arguments["authentication_header"],
        ),
    )
    # polling_for_couple_mode_thread.start()
    polling_for_interval_thread.start()
    sending_sensor_data_thread.start()

    # polling_for_couple_mode_thread.join()
    polling_for_interval_thread.join()
    sending_sensor_data_thread.join()
