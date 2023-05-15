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
import myble
import sys

lock = threading.Lock()
interval = 20


def init()->None:
    """
    This function intialises the database,
    check the credentials, asks if the
    accesspoint is enabled.
    If the accesspoint has no credentails
    or has some which are not known
    to the webserver it will register
    a new one
    ---------
    Returns None
    """

    # read the conf.yaml file
    config = configparser.ConfigParser()
    config.read_file(open(r"conf.yaml"))  # TODO: change on raspberry
    address = config.get("config", "address")
    global interval
    interval = int(config.get("config", "interval"))
    logger.log_info("start interval: " + str(interval))
    name = config.get("config", "name")

    # establish the database-connection
    path = "database.db"  # TODO: change on raspberry
    conn = db.access_database(path)
    db.create_tables(conn)

    # Read name and password from identification.yaml
    try:
        login = credentials.read_from_yaml()

    except:
        sys.exit(
            "Error when reading identification.yaml. Please make sure that the file is in the same directory as this script"
        )

    #Check if accesspoint exists on webserver, if not create a new one
    if login is not None and login[0] is not None:
        verify_credentials = rci.request_if_accesspoint_exists(address, login[0])
        if verify_credentials != True:
            print("Credentials not recognized, register as new accesspoint")
            login = None

    #Register new accesspoint if necessary
    if login is None or login[0] is None or login[1] is None:
        print("Register as new accesspoint")
        login = register_accesspoint(address, interval, name)
    print(login)
    auth_header = rci.prepare_auth_headers(login[0], login[1])

    # This loop checks for approval from the webserver
    while True:
        enabled = rci.request_approval(address, auth_header)
        if enabled is True:
            print("Successfully authenticated")
            break
        print("waiting for authentication")
        time.sleep(10)

    print(login)
    logger.log_info("Login as: " + login[0] + " " + login[1])
    auth_header = rci.prepare_auth_headers(login[0], login[1])
    argument_list = {
        "authentication_header": auth_header,
        "address": address,
        "db_path": path,
        # "interval": interval,
    }
    return argument_list


def main(args):
    """
    This function starts all the
    threads.
     Arguments
    ---------
    args: a dictionary containing:
    authentication_header: str
        the auth_header for the rest-connection
    address: str,
    db_path: str,
    ---------
    Returns None
    """
   
    polling_for_couple_mode_thread = Thread(
        target=poll_couple_mode,
        args=(args["db_path"], args["address"], args["authentication_header"]),
    )
    polling_for_interval_thread = Thread(
        target=poll_interval,
        args=(
            args["address"],
            args["authentication_header"],
        ),
    )

    polling_for_limits_thread = Thread(
        target=poll_limits,
        args=(
            args["address"],
            args["authentication_header"],
        ),
    )
    sending_sensor_data_thread = Thread(
        target=send_sensor_data,
        args=(
            args["address"],
            args["authentication_header"],
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
    polling_for_limits_thread.join()
    sending_sensor_data_thread.join()
    polling_enable_for_sensorstation_thread.join()


def poll_couple_mode(path: str, address: str, auth_header: str):
    """
    This function polls an checks
    if the couple mode has been activated
     Arguments
    ---------
    path: str,
    address: str,
    authentication_header: str
        the auth_header for the rest-connection
    ---------
    Returns None
    """
    
    while True:
        start_coupling = rci.request_couple_mode(address, auth_header)
        if start_coupling is True:
            logger.log_info("Starting coupling mode")
            myble.ble_function(path, address, auth_header)
        time.sleep(30)


def poll_interval(address: str, auth_header: str):
    """
    This function polls an checks
    if the interval has changed
    Arguments
    ---------
    address: str,
    authentication_header: str
        the auth_header for the rest-connection
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
        print("interval:",new_interval)
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

        time.sleep(30)


def poll_limits(address: str, auth_header: str):
    """
    This function polls to check
    if the limits have changed
    Arguments
    ---------
    address: str,
    authentication_header: str
        the auth_header for the rest-connection
    ---------
    Returns None
    """

    path = "database.db"  # TODO: change on raspberry
    conn = db.access_database(path)

    
    while True:
        list_of_sensorstations = db.get_all_sensorstations(conn)
        for sensorstation_id in list_of_sensorstations:
            new_limits = rest.request_limits(
                address, auth_header, int(sensorstation_id)
            )

            if new_limits is None:
                continue
            for list in new_limits:
                type_limit = list["dataType"]
                min_limit = list["minLimit"]
                max_limit = list["maxLimit"]
                
                #print(list)
                   
                db.update_limits(
                    conn, int(sensorstation_id), type_limit, min_limit, max_limit
                )
                    
        time.sleep(60)


def send_sensor_data(address: str, auth_header: str):
    """
    This function sends sensor_data
    and then deletes them from the database
    Arguments
    ---------
    address: str,
    authentication_header: str
        the auth_header for the rest-connection
    ---------
    Returns None
    """

    path = "database.db"  # TODO: change on raspberry
    conn = db.access_database(path)

    while True:
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
                    "Found no records to send for station with id "
                    + str(sensorstation_id)
                )

        lock.acquire()
        try:
            my_interval = interval
            logger.log_info("interval in send_data: " + str(my_interval))

        except Exception as e:  
            logger.log_error("Reading global interval in sending_data failed: " + str(e))
        finally:
            lock.release()
        time.sleep(my_interval)


def poll_sensorstation_enabled(address: str, auth_header: str):
    """
    This function poll if the sensorstations 
    are enabled
    Arguments
    ---------
    address: str,
    authentication_header: str
        the auth_header for the rest-connection
    ---------
    Returns None
    """
    path = "database.db"  # TODO: change on raspberry
    conn = db.access_database(path)

    while True:
        list_of_sensorstations = db.get_all_sensorstations(conn)
        for sensorstation_id in list_of_sensorstations:
            response = rest.request_if_is_sensorstation_enabled(
                address, auth_header, int(sensorstation_id)
            )
            #print(response, " ", int(sensorstation_id))

        time.sleep(60)


def register_accesspoint(address: str, interval: int, name: str):
    """
    Register a new accesspoint
    Arguments
    ---------
    address: str,
    interval: int,
    name: str
    ---------
    Returns None
    """

    login = None
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
    return login


if __name__ == "__main__":
    arguments = init()
    main(arguments)
