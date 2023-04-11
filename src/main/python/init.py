import dbconnection as db
import configparser
import restcontroller_init as rci
import credentials
import time

def main():
    #read the conf.yaml file
    config = configparser.ConfigParser()
    config.read_file(open(r"/home/benedikt/OneDrive/SS2023/Softwareengineering/PS/g1t2/src/main/python/conf.yaml"))      #TODO: change on raspberry
    address = config.get("config", "address")
    interval = config.get("config", "interval")
    name = config.get("config", "name")

    #establish the database-connection
    path = "/home/benedikt/OneDrive/SS2023/Softwareengineering/PS/g1t2/src/main/python/database.db"                     #TODO: change on raspberry
    conn = db.create_database(path)

    #esteblish a (first time) connection the the webserver
    login = credentials.read_from_yaml()
    while(login[0] is None or login[1] is None):
        try:
            login = rci.register_access_point_at_server(address, interval, name)
            credentials.write_to_yaml(login[0], login[1])
        except:
            print("No connection possible, trying again in 5sec...")
            print("To change the credentials, modify the identification.yaml file and restart the program")
            time.sleep(5)

if __name__ == '__main__':
    main()
