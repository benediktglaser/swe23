import asyncio
import time
import re
import threading
from threading import Thread
from bleak import BleakScanner, BLEDevice, BleakClient
from bleak.backends.scanner import AdvertisementData
import dbconnection as db
import restcontroller_init as rci
import restcontroller as rc
import struct
from sensordata import SensorData
import logger

device_name_prefix = "Station G1T2 "
# all already connected SensorStations
CONNECTED_DEVICES = set()
# all newly scanned SensorStations
SCANNED_DEVICES = set()
# Database connection
PATH = ""
# Webserver address
ADDRESS = ""
# Auth_header for rest
AUTH_HEADER = ""
# mutex on connected and scanned devices
lock = threading.Lock()
# UUIDS of BLE characteristics
DATA_UUIDS = {"temp": "00000000-0000-0000-0000-0000004102a0",
              "pressure": "00000000-0000-0000-0000-0000004102b0",
              "humid": "00000000-0000-0000-0000-0000004102c0",
              "quality": "00000000-0000-0000-0000-0000004102d0",
              "soil": "00000000-0000-0000-0000-0000004102e0",
              "light": "00000000-0000-0000-0000-0000004102f0"}
LIMIT_UUIDS = {"temp": "00000000-0000-0000-0000-0000004102aa",
               "pressure": "00000000-0000-0000-0000-0000004102bb",
               "humid": "00000000-0000-0000-0000-0000004102cc",
               "quality": "00000000-0000-0000-0000-0000004102dd",
               "soil": "00000000-0000-0000-0000-0000004102ee",
               "light": "00000000-0000-0000-0000-0000004102ff"}
# UUID of characteristic whether gardener is at SensorStation
GARDENER_UUID = "00000000-0000-0000-0000-000000410290"
# How long the limit should be exceeded so that it's displayed on the SensorStation (in sec)
EXCESS_LIMIT = 20


def ble_function(path, address, auth_header):
    """
    Function which sets the DB-Connection and the authentication for REST and starts scanning.

    Arguments
    ----------
    path: str
        Path to the Database
    address: str
        Address of the Webserver
    auth_header: str
        Authentication-Header for HTTP-Basic
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
    SCANNED_DEVICES = set()
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

    Arguments
    ----------
    device: BLEDevice
        Scanned Device
    advertisement: AdvertisementData
        Advertisement for scanned Device
    """

    if is_new_sensor_station(device, advertisement):
        await establish_connection(device)


# Filtering for connection
# Only connect to devices named "Station G1T2 XXX" - XXX being the DIP-Id.
# DIP-Id may not be duplicate
def is_new_sensor_station(device: BLEDevice, advertisement: AdvertisementData) -> bool:
    """
    Checks if the Advertisement is of one of our SensorStations based on the local_name with a DIP-ID
    which is available on this AccessPoint and hasn't already been advertised.

    Arguments
    ----------
    device: BLEDevice
        Scanned Device
    advertisement: AdvertisementData
        Advertisement for scanned Device
    Returns
    ----------
    bool:
        True if device hasn't been scanned and hasn't been connected yet
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
    SCANNED_DEVICES.add(device)
    lock.release()
    return True


async def establish_connection(device: BLEDevice):
    """
    Starts a Thread for each new SensorStation.

    Arguments
    ----------
    device: BLEDevice
        SensorStation
    """

    thread = Thread(target=ble_thread, args=[device])
    thread.start()


def ble_thread(device: BLEDevice):
    """
    Thread function for each SensorStation which polls for Verification and calls services.

    Arguments
    ----------
    device: BLEDevice
        SensorStation
    """

    already_connected, verified = poll_for_verification(device)
    if already_connected or verified:
        if not already_connected and not dip_is_available(device):
            return
        CONNECTED_DEVICES.add(device)
        SCANNED_DEVICES.remove(device)
        lock.release()
        asyncio.run(call_services(device, already_connected))


def poll_for_verification(device: BLEDevice) -> (bool, bool):
    """
    Poll for the verification of this SensorStation on the Webserver.

    Arguments
    ----------
    device: BLEDevice
        SensorStation

    Returns
    -------
    (bool, bool):
        True if Device was already connected once
        True if Device was verified within timelimit
    """

    verified = False
    dip = get_dip_from_device(device)
    # rest-request with dipId and MAC
    # check returnBody if SensorStation is available (not already connected to and verified by other AccessPoint)
    response = rci.propose_new_sensorstation_at_server(ADDRESS, dip, device.address, AUTH_HEADER)
    if response["alreadyConnected"]:
        return True, False
    elif not response["available"]:
        return False, False
    timeout = time.time() + 60 * 5
    while not verified:
        # poll if device is verified for this AccessPoint
        verified = rci.request_sensorstation_if_verified(ADDRESS, dip, AUTH_HEADER)
        time.sleep(5)
        if time.time() > timeout:
            logger.log_info(f"Polling for Verification of {device.name} timed out")
            return False
        if not dip_is_available(device):
            return False
        lock.release()
    return False, verified


def dip_is_available(device: BLEDevice) -> bool:
    """
    Check whether Dip-ID is still available and not already taken by other Device.

    Arguments
    ----------
    device: BLEDevice
        SensorStation

    Returns
    -------
    bool:
        True if Dip-ID is free.
    """

    lock.acquire()
    available = all(d.name != device.name for d in CONNECTED_DEVICES)
    if not available:
        lock.release()
    return available


def get_dip_from_device(device: BLEDevice) -> int:
    """
    Gets Dip-ID from device.

    Arguments
    ----------
    device: BLEDevice
        SensorStation

    Returns
    -------
    int:
        Dip-ID
    """

    return int(re.split(device_name_prefix, device.name)[1])


async def call_services(device: BLEDevice, already_connected: bool):
    """
    Uses the services of the BLEDevice to read Data and send limit excess.

    Arguments
    ----------
    device: BLEDevice
        SensorStation
    already_connected: bool
        Whether SensorStation was already connected once with this AccessPoint

    """

    conn = db.access_database(PATH)
    dip = get_dip_from_device(device)
    excesses = {"temp": 0,
                "pressure": 0,
                "humid": 0,
                "quality": 0,
                "soil": 0,
                "light": 0}
    active_excess = False
    # client == sensor_station
    client = BleakClient(device, timeout=30, disconnected_callback=my_disconnected_callback)
    if not already_connected:
        rci.register_new_sensorstation_at_server(ADDRESS, dip, AUTH_HEADER)
        db.init_limits(conn, dip, device.address)
    data = SensorData(dip, 0, 0, 0, 0, 0, 0)
    while True:
        try:
            if not client.is_connected:
                logger.log_info(f"Connecting to {device.name}")
                await client.connect()
                with lock:
                    CONNECTED_DEVICES.add(device)
                logger.log_info(f"Connected to {device.name}")
            time.sleep(10)
            for data_type in DATA_UUIDS.keys():
                try:
                    uuid = DATA_UUIDS[data_type]
                    value = struct.unpack('d', await client.read_gatt_char(uuid))[0]
                    data.set_value(data_type, value)

                    if not active_excess:
                        lower, upper = db.get_limits(conn, dip, data_type)
                        excess = db.calculate_limit(value, lower, upper)

                        if abs(excess - 1.0) > 0.001:
                            try:
                                gardener_is_here = struct.unpack('?', await client.read_gatt_char(GARDENER_UUID))[0]
                                if gardener_is_here:
                                    continue
                            except Exception as e:
                                logger.log_error(f"Error reading gardener characteristic: {e}")
                            excesses[data_type] += 1
                            # because it takes 10s to read data we can check for how many iterations there was an excess
                            if excesses[data_type] >= EXCESS_LIMIT / 10:
                                active_excess = True
                                excesses = {"temp": 0,
                                            "pressure": 0,
                                            "humid": 0,
                                            "quality": 0,
                                            "soil": 0,
                                            "light": 0}
                                try:
                                    await client.write_gatt_char(LIMIT_UUIDS[data_type], struct.pack('I', int(excess * 100)))
                                    await client.read_gatt_char(LIMIT_UUIDS[data_type])
                                except Exception as e:
                                    logger.log_error(f"Error reading limit characteristic of {data_type}: {e}")
                        else:
                            excesses[data_type] = 0
                except Exception as e:
                    logger.log_error(f"Error reading characteristic {data_type}: {e}")
            if active_excess:
                try:
                    gardener_is_here = struct.unpack('?', await client.read_gatt_char(GARDENER_UUID))[0]
                    if gardener_is_here:
                        active_excess = False
                        rc.gardener_is_at_station(ADDRESS, dip, AUTH_HEADER)
                except Exception as e:
                    logger.log_error(f"Error reading gardener characteristic: {e}")
            db.insert_sensor_data(conn, data)
        except (asyncio.exceptions.CancelledError, asyncio.exceptions.TimeoutError):
            logger.log_error("Establishing BLE connection timed out")
        finally:
            with lock:
                if device in CONNECTED_DEVICES:
                    CONNECTED_DEVICES.remove(device)


def my_disconnected_callback(client: BleakClient):
    """
    Logs disconnect from SensorStation.

    Parameters
    ----------
    client: BLEClient
        Needed so that this method can be registered as disconnect_callback
        in BLEClient

    """

    logger.log_error("Disconnected")


def reconnect_thread(path: str, address: str, auth_header: str, mac: str):
    """
    In order to reconnect to device after restarting of AccessPoint.

    Arguments
    ----------
    path: str
        Path to the Database
    address: str
        Address of the Webserver
    auth_header: str
        Authentication-Header for HTTP-Basic
    mac: str
        MAC-Address of the Device

    """

    global PATH
    global ADDRESS
    global AUTH_HEADER
    PATH = path
    ADDRESS = address
    AUTH_HEADER = auth_header
    asyncio.run(search_for_device(mac))


async def search_for_device(mac: str):
    """
    Searches for a BLEDevice by MAC-Address and calls services.

    Arguments
    ----------
    mac: str
        MAC-Address of the Device.
    """
    device = None
    while device is None:
        logger.log_info("Trying to find device")
        device = await BleakScanner.find_device_by_address(mac)
    if device is not None:
        await call_services(device, already_connected=True)
