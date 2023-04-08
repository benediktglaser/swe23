import asyncio
import sqlite3
import threading
from bleak import BleakScanner, BLEDevice, BleakClient
from bleak.backends.scanner import AdvertisementData
from dbconnection import insert_sensor_data

device_name_prefix = "SensorStation G1T2"
# all already connected SensorStations
DEVICES = []
# mutex on DEVICES
lock = threading.Lock()


# TODO: don't use easy method of bleakScanner because it doesn't work with multiple devices at the same time
# Filtering for connection
# Only connect to devices named "SensorStation G1T2 [X][X]X" - XXX being the DIP-Id.
# DIP-Id may not be duplicate
def filter_func(device: BLEDevice, advertisement: AdvertisementData) -> bool:
    if advertisement.local_name is None or not advertisement.local_name.startswith(device_name_prefix):
        return False

    lock.acquire()
    if any(d.name == advertisement.local_name for d in DEVICES):
        return False
    return True


async def establish_connection():
    device = await BleakScanner.find_device_by_filter(filter_func)  # could also have timeout
    if device is None:
        print("ERROR: Could not find a device")
        lock.release()
        return

    DEVICES.append(device)
    lock.release()

    return device


async def call_services(conn, client):
    print("Inside call_services()")
    print(DEVICES)
    await asyncio.sleep(3)


async def ble_function(conn):
    """Function which establishes connection and calls ble services

    :param conn: Connection to the Database
    :type conn: sqlite3.Connection

    :returns: Nothing
    """

    device = await establish_connection()
    if device is None:
        return
    # client == sensor_station
    async with BleakClient(device) as client:
        print("Connected to device {0}".format(device.name))
        await call_services(conn, client)
        print("INFO: Disconnecting from device {0} ...".format(device.name))
    print("INFO: Disconnected from device {0}".format(device.name))


def ble_thread(conn):
    """Thread function for each sensor_station

    :param conn: Connection to the Database
    :type conn: sqlite3.Connection

    :returns:
        Nothing
    """

    asyncio.run(ble_function(conn))
    print("Thread finished")
