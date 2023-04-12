import dbconnection as db
import configparser
import restcontroller_init as rci
import credentials
import time


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
    }
    return argument_list


def poll_couple_mode(address: str, auth_header):
    while True:
        start_coupling = rci.request_couple_mode(address, auth_header)
        print("Coupling:",start_coupling)
        time.sleep(10)
        break


def main():
    # TODO wait for first Sensorstation
    pass


if __name__ == "__main__":
    arguments = init()
    poll_couple_mode(arguments["address"], arguments["authentication_header"])
