# This file is only used for testing manually if ble works
import asyncio

import myble
import dbconnection as db
from threading import Thread


if __name__ == '__main__':
    path = "database.db"
    db.delete_database(path)
    conn = db.create_database(path)
    myble.ble_function(conn)
