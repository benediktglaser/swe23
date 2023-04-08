# This file is only used for testing manually if ble works

import myble
import dbconnection as db
from threading import Thread


if __name__ == '__main__':
    path = "database.db"
    db.delete_database(path)
    conn = db.create_database(path)
    # myble.ble_thread(conn)
    t1 = Thread(target=myble.ble_thread, args=[conn])
    t2 = Thread(target=myble.ble_thread, args=[conn])
    t1.start()
    print("1started")
    t2.start()
    print("Both started")
    t1.join()
    print("1 joined")
    t2.join()
