import configparser
import time
import restcontroller as rest
import sensordata
import logger
from threading import Thread
import threading
import credentials
import dbconnection as db
import restcontroller_init as rci

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
            # TODO call Andis methods
            # register the found just for debug to show how it would work
            response = rci.propose_new_sensorstation_at_server(
                address, 93, "churchey", auth_header
            )
            if response != None:
                print("Got that response ", response)
                while True:
                    verified_response = rci.request_sensorstation_if_verified(
                        address, 93, auth_header
                    )
                    if verified_response.__bool__:
                        print("verified")
                        # send register call
                        final_resp = rci.register_new_sensorstation_at_server(
                            address, 93, auth_header
                        )
                        if final_resp.__bool__:
                            print("all worked")
                            break

        # DEBUG ONLY
        # i = i + 1
        # if i == 6:
        break
        time.sleep(20)


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


def poll_limits(address: str, auth_header: str):
    i = 1
    path = "database.db"  # TODO: change on raspberry
    conn = db.create_database(path)

    # Just for testing
    # data_1 = sensordata.SensorData(1, 8 * i, 9 * i, 10 * i, 11 * i, 12 * i, 13 * i)
    # data_2 = sensordata.SensorData(2, 8 * i, 9 * i, 10 * i, 11 * i, 12 * i, 13 * i)
    # db.insert_sensor_data(conn, data_1)
    # db.insert_sensor_data(conn, data_2)

    while True:
        list_of_sensorstations = db.get_all_sensorstations(conn)
        for sensorstation_id in list_of_sensorstations:
            new_limits = rest.request_limits(
                address, auth_header, int(sensorstation_id)
            )
            for list in new_limits:
                type_limit = list["dataType"]
                min_limit = list["minLimit"]
                max_limit = list["maxLimit"]
                db.update_limits(
                    conn, int(sensorstation_id), type_limit, min_limit, max_limit
                )
        # Just for Debug
        i = i + 1
        if i == 5:
            print(db.get_limits(conn, 1, "all"))
            print(db.get_limits(conn, 2, "all"))
            break
        # End of Debug
        time.sleep(15)


def send_sensor_data(address: str, auth_header: str):
    i = 1
    path = "database.db"  # TODO: change on raspberry
    conn = db.create_database(path)

    # Debug
    db.init_limits(conn, 1)
    db.init_limits(conn, 2)
    # Debug end
    while True:
        # Debug
        data_1 = sensordata.SensorData(1, 8 * i, 9 * i, 10 * i, 11 * i, 12 * i, 13 * i)
        data_2 = sensordata.SensorData(
            2, 18 * i, 19 * i, 110 * i, 111 * i, 112 * i, 113 * i
        )
        db.insert_sensor_data(conn, data_1)
        db.insert_sensor_data(conn, data_2)
        # Debug end
        list_of_sensorstations = db.get_all_sensorstations(conn)
        for sensorstation_id in list_of_sensorstations:

            list = db.get_sensor_data(conn, sensorstation_id)
            if len(list) != 0:
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
            else:
                logger.log_info(
                    "Found no records to send for station with id " + sensorstation_id
                )
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


def poll_sensorstation_enabled(address: str, auth_header: str):
    i = 1
    path = "database.db"  # TODO: change on raspberry
    conn = db.create_database(path)
    # Debug
    db.init_limits(conn, 1)
    db.init_limits(conn, 2)

    while True:
        list_of_sensorstations = db.get_all_sensorstations(conn)
        for sensorstation_id in list_of_sensorstations:
            response = rest.request_if_is_sensorstation_enabled(
                address, auth_header, int(sensorstation_id)
            )
            print(response, " ", int(sensorstation_id))

        # Just for Debug
        i = i + 1
        if i == 5:
            break
        # End of Debug
        time.sleep(15)


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

    polling_for_limits_thread = Thread(
        target=poll_limits,
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

    polling_enable_for_sensorstation_thread = Thread(
        target=poll_sensorstation_enabled,
        args=(
            arguments["address"],
            arguments["authentication_header"],
        ),
    )
    polling_for_couple_mode_thread.start()
    polling_for_interval_thread.start()
    polling_for_limits_thread.start()
    sending_sensor_data_thread.start()
    polling_enable_for_sensorstation_thread.start()

    polling_for_couple_mode_thread.join()
    polling_for_interval_thread.join()
    sending_sensor_data_thread.join()
    polling_for_limits_thread.join()
    polling_enable_for_sensorstation_thread.join()
