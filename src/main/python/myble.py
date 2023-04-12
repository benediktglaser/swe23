import asyncio
import time
import threading
from threading import Thread
from bleak import BleakScanner, BLEDevice, BleakClient
from bleak.backends.scanner import AdvertisementData

device_name_prefix = "SensorStation G1T2"
# all already connected SensorStations
DEVICES = []
# Database connection
CONN = None
# mutex on DEVICES
lock = threading.Lock()


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


async def my_callback(device: BLEDevice, advertisement: AdvertisementData):
    if is_sensor_station(advertisement):
        await establish_connection(device)


async def scan_for_devices():
    scanner = BleakScanner(detection_callback=my_callback)
    await scanner.start()
    timeout = time.time() + 60 * 5
    while time.time() <= timeout:
        # Poll Pairing-Mode from Webserver
        await asyncio.sleep(1)
    await scanner.stop()


async def establish_connection(device):
    print("Establish Connection")
    DEVICES.append(device)
    lock.release()
    thread = Thread(target=ble_thread, args=[device])
    thread.start()


async def call_services(device: BLEDevice):
    """

    :param device: SensorStation
    :type device: BLEDevice
    """
    # client == sensor_station
    async with BleakClient(device) as client:
        print("Connected to device {0}".format(device.name))
        for i in range(3):
            print(f"Service {i}")
            await asyncio.sleep(3)
        print("INFO: Disconnecting from device {0} ...".format(device.name))
    print("INFO: Disconnected from device {0}".format(device.name))


def ble_function(conn):
    """
    Function which set the DB-Connection and starts scanning

    :param conn: Connection to the Database
    :type conn: sqlite3.Connection
    """

    global CONN
    CONN = conn
    asyncio.run(scan_for_devices())


def ble_thread(device: BLEDevice):
    """Thread function for each sensor_station which polls for Verification and calls services

    :param device: SensorStation
    :type device: BLEDevice
    """

    verified = False
    # TODO: send device to Webserver and poll for verification
    # rest-request with MAC and dipId
    # check returnBody if SensorStation is available (not already advertised by other AP)
    timeout = time.time() + 60*5
    while (not verified) and time.time() <= timeout:
        # poll if device is verified for this AccessPoint
        print("Poll for Verification")
        verified = True
        pass
    # timed out
    if time.time() > timeout:
        print("ERROR: BLE-Connection timed out")
        return
    asyncio.run(call_services(device))
    print("Thread finished")
