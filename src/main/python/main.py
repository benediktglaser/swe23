import dbconnection as db
import configparser
import restcontroller_init as rci
import credentials
import time
import restcontroller as rest
from threading import Thread


def init():
    # read the conf.yaml file
    config = configparser.ConfigParser()
    config.read_file(open(r"conf.yaml"))  # TODO: change on raspberry
    address = config.get("config", "address")
    interval = config.get("config", "interval")
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
            time.sleep(5)
        auth_header = rci.prepare_auth_headers(login[0], login[1])

        # This is the loop
        while True:
            enabled = rci.request_approval(address, auth_header)
            if enabled:
                break
            print("waiting for authentication")
            time.sleep(10)

    print(login)
    auth_header = rci.prepare_auth_headers(login[0], login[1])
    argument_list = {
        "connection": conn,
        "authentication_header": auth_header,
        "address": address,
        "interval": interval,
    }
    return argument_list


def poll_couple_mode(address: str, auth_header):
    i = 0
    while True:
        start_coupling = rci.request_couple_mode(address, auth_header)
        print("Coupling:", start_coupling)
        if start_coupling is True:
            # TODO call real coupling method
            pass
        if i == 1:
            dummy_couple_method()

        time.sleep(5)
        i = i + 1
        if i == 6:
            break


def dummy_couple_method():
    print("working in couple method")
    time.sleep(10)


def poll_interval(address: str, interval: float, auth_header):
    current_interval = interval
    i = 0
    while True:
        new_interval = rest.request_interval(address, auth_header)
        print("interval:", interval)
        if interval is not None and new_interval is not interval:
            # TODO call real coupling method
            current_interval = new_interval

        time.sleep(5)
        i = i + 1
        if i == 6:
            break


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
            arguments["interval"],
            arguments["authentication_header"],
        ),
    )
    polling_for_couple_mode_thread.start()
    polling_for_interval_thread.start()
    polling_for_couple_mode_thread.join()
    polling_for_interval_thread.join()
