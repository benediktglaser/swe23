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
CONNECTED_DEVICES = []
# all newly scanned SensorStations
SCANNED_DEVICES = []
# Database connection
PATH = None
# Webserver address
ADDRESS = ""
# Auth_header for rest
AUTH_HEADER = ""
# mutex on connected and scanned devices
lock = threading.Lock()


def ble_function(path, address, auth_header):
    """
    Function which sets the DB-Connection and the authentication for REST and starts scanning.

    :param path: Path to the Database
    :param address: Address of the Webserver
    :param auth_header: Authentication-Header for HTTP-Basic

    """

    global PATH
    PATH = path
    global ADDRESS
    ADDRESS = address
    global AUTH_HEADER
    AUTH_HEADER = auth_header
    asyncio.run(scan_for_devices())


async def scan_for_devices():
    """
    Starts BleakScanner and calls :func:`my_callback` for each found BLEDevice.
    Stops scanning after 5 minutes or after polling mode is turned off on Webserver.

    """

    scanner = BleakScanner(detection_callback=my_callback)
    global SCANNED_DEVICES
    SCANNED_DEVICES = []
    await scanner.start()
    timeout = time.time() + 60 * 5
    while time.time() <= timeout:
        if not rci.request_couple_mode(ADDRESS, AUTH_HEADER):
            break
        await asyncio.sleep(10)
    await scanner.stop()


async def my_callback(device: BLEDevice, advertisement: AdvertisementData):
    """
    Calls connection establishment function if the found BLEDevice is a SensorStation with available DIP-ID

    :param device: Scanned BLEDevice
    :param advertisement: Advertisement for scanned Device
    """

    if is_new_sensor_station(device, advertisement):
        await establish_connection(device)


# Filtering for connection
# Only connect to devices named "SensorStation G1T2 [X][X]X" - XXX being the DIP-Id.
# DIP-Id may not be duplicate
def is_new_sensor_station(device: BLEDevice, advertisement: AdvertisementData) -> bool:
    """
    Checks if the Advertisement is of one of our SensorStations based on the local_name with a DIP-ID
    which is available on this AccessPoint and hasn't already been advertised.

    :param device: Scanned BLEDevice
    :param advertisement: Advertisement for scanned BLEDevice
    :return: bool
    """

    if advertisement.local_name is None or not advertisement.local_name.startswith(device_name_prefix):
        return False

    lock.acquire()
    if any(d.name == advertisement.local_name for d in CONNECTED_DEVICES):
        lock.release()
        return False
    # in order to avoid calling my_callback multiple times per device
    if any(d.address == device.address for d in SCANNED_DEVICES):
        lock.release()
        return False
    SCANNED_DEVICES.append(device)
    lock.release()
    return True


async def establish_connection(device: BLEDevice):
    """
    Starts a Thread for each new SensorStation.

    :param device: SensorStation to connect
    """

    thread = Thread(target=ble_thread, args=[device])
    thread.start()


def ble_thread(device: BLEDevice):
    """
    Thread function for each SensorStation which polls for Verification and calls services.

    :param device: SensorStation
    """

    if poll_for_verification(device):
        if not dip_is_available(device):
            return
        CONNECTED_DEVICES.append(device)
        lock.release()
        asyncio.run(call_services(device))


def poll_for_verification(device: BLEDevice) -> bool:
    """
    Poll for the verification of this SensorStation on the Webserver.

    :param device: SensorStation
    :return: Whether device was verified within timelimit of 5 minutes
    """

    verified = False
    dip = get_dip_from_device(device)
    # rest-request with dipId and MAC
    # check returnBody if SensorStation is available (not already connected to and verified by other AccessPoint)
    response = rci.propose_new_sensorstation_at_server(ADDRESS, dip, device.address, AUTH_HEADER)
    if response["alreadyConnected"]:
        return True
    elif not response["available"]:
        return False
    timeout = time.time() + 60 * 5
    while not verified:
        # poll if device is verified for this AccessPoint
        verified = rci.request_sensorstation_if_verified(ADDRESS, dip, AUTH_HEADER)
        time.sleep(5)
        if time.time() > timeout:
            print("Error: Polling for Verification timed out")
            return False
        if not dip_is_available(device):
            return False
        lock.release()
    return verified


def dip_is_available(device: BLEDevice) -> bool:
    """
    Check whether DIP-ID is still available and not already taken by other Device.

    :param device: SensorStation
    :return: bool
    """

    lock.acquire()
    available = all(d.name != device.name for d in CONNECTED_DEVICES)
    if not available:
        lock.release()
    return available


def get_dip_from_device(device: BLEDevice) -> int:
    """
    Gets the DIP-ID from the device.name.

    :param device: SensorStation
    :return: DIP-ID
    """

    return int(re.split(device_name_prefix, device.name)[1])


async def call_services(device: BLEDevice):
    """
    Calls the services of the BLEDevice.

    :param device: SensorStation
    :type device: BLEDevice
    """

    conn = db.access_database(PATH)
    dip = get_dip_from_device(device)
    print("Connecting")
    # client == sensor_station
    try:
        async with BleakClient(device, timeout=10) as client:
            print("Connected to device {0}".format(device.name))
            rci.register_new_sensorstation_at_server(ADDRESS, dip, AUTH_HEADER)
            db.init_limits(conn, dip)
            for i in range(3):
                print(f"Service {i}")
                await asyncio.sleep(3)
            print("INFO: Disconnecting from device {0} ...".format(device.name))
        #     TODO: remove limit-entry of SensorStation
        print("INFO: Disconnected from device {0}".format(device.name))
    except (asyncio.exceptions.CancelledError, asyncio.exceptions.TimeoutError) as e:
        print("Error: Establishing BLE connection timed out")
    finally:
        lock.acquire()
        SCANNED_DEVICES.remove(device)
        CONNECTED_DEVICES.remove(device)
        lock.release()
