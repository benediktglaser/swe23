import asyncio
import time
import re
import threading
from threading import Thread
from bleak import BleakScanner, BLEDevice, BleakClient, BleakError
from bleak.backends.scanner import AdvertisementData
import dbconnection as db
import restcontroller_init as rci

device_name_prefix = "SensorStation G1T2 "
# all already connected SensorStations
DEVICES = []
# Database connection
CONN = None
# Webserver address
ADDRESS = ""
# Auth_header for rest
AUTH_HEADER = ""
# mutex on DEVICES
lock = threading.Lock()


async def ble_function(address, auth_header):
    """
    Function which set the DB-Connection and starts scanning

    """

    global CONN
    CONN = db.create_database("database.db")
    global ADDRESS
    ADDRESS = address
    global AUTH_HEADER
    AUTH_HEADER = auth_header
    await scan_for_devices()


async def scan_for_devices():
    scanner = BleakScanner(detection_callback=my_callback)
    await scanner.start()
    timeout = time.time() + 60 * 5
    while time.time() <= timeout:
        if not rci.request_couple_mode(ADDRESS, AUTH_HEADER):
            break
        await asyncio.sleep(10)
    await scanner.stop()


async def my_callback(device: BLEDevice, advertisement: AdvertisementData):
    if is_sensor_station(advertisement):
        await establish_connection(device)


# Filtering for connection
# Only connect to devices named "SensorStation G1T2 [X][X]X" - XXX being the DIP-Id.
# DIP-Id may not be duplicate
def is_sensor_station(advertisement: AdvertisementData) -> bool:
    """

    :param advertisement: Scanned Advertisement
    :return: bool
    """
    if advertisement.local_name is None or not advertisement.local_name.startswith(device_name_prefix):
        return False

    lock.acquire()
    if any(d.name == advertisement.local_name for d in DEVICES):
        lock.release()
        return False
    return True


async def establish_connection(device):
    print("Establish Connection to " + device.name)
    DEVICES.append(device)
    lock.release()
    thread = Thread(target=ble_thread, args=[device])
    thread.start()


def ble_thread(device: BLEDevice):
    """Thread function for each sensor_station which polls for Verification and calls services

    :param device: SensorStation
    :type device: BLEDevice
    """

    if poll_for_verification(device):
        # TODO: move DEVICES.append here
        asyncio.run(call_services(device))
    print("Thread finished")


def get_dip_from_device(device: BLEDevice) -> int:
    return int(re.split(device_name_prefix, device.name)[1])


def poll_for_verification(device: BLEDevice) -> bool:
    verified = False
    dip = get_dip_from_device(device)
    if not rci.propose_new_sensorstation_at_server(ADDRESS, dip, device.address, AUTH_HEADER):
        return False
    # rest-request with MAC and dipId
    # check returnBody if SensorStation is available (not already advertised by other AP)
    timeout = time.time() + 60 * 5
    while not verified:
        # poll if device is verified for this AccessPoint
        print("Poll for Verification")
        verified = rci.request_sensorstation_if_verified(ADDRESS, dip, AUTH_HEADER)
        time.sleep(5)
        if time.time() > timeout:
            print("Error: Polling for Verification timed out")
            return False
    return verified


async def call_services(device: BLEDevice):
    """

    :param device: SensorStation
    :type device: BLEDevice
    """

    dip = get_dip_from_device(device)
    # client == sensor_station
    try:
        async with BleakClient(device, timeout=10) as client:
            print("Connected to device {0}".format(device.name))
            rci.register_new_sensorstation_at_server(ADDRESS, dip, AUTH_HEADER)
            for i in range(3):
                print(f"Service {i}")
                await asyncio.sleep(3)
            print("INFO: Disconnecting from device {0} ...".format(device.name))
        print("INFO: Disconnected from device {0}".format(device.name))
    except (asyncio.exceptions.CancelledError, asyncio.exceptions.TimeoutError) as e:
        print("Error: Establishing BLE connection timed out")
    finally:
        lock.acquire()
        DEVICES.remove(device)
        lock.release()
