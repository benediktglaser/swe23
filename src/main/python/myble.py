import asyncio
import time
import re

import bleak.exc
from asyncio_taskpool import TaskPool
from bleak import BleakScanner, BLEDevice, BleakClient
from bleak.backends.scanner import AdvertisementData
import dbconnection as db
import restcontroller_init as rci
import restcontroller as rc
import struct
from sensordata import SensorData
import logger

device_name_prefix = "Station G1T2 "
# all newly scanned SensorStations
SCANNED_DEVICES = set()
# Database connection
CONN = None
# Webserver address
ADDRESS = ""
# Auth_header for rest
AUTH_HEADER = ""
# Taskpool for all sensor_stations
TASKPOOL = TaskPool()
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


def start_ble(path, address, auth_header):
    """
    Function which sets the DB-Connection and the authentication for REST and starts the BLE function.

    Arguments
    ----------
    path: str
        Path to the Database
    address: str
        Address of the Webserver
    auth_header: str
        Authentication-Header for HTTP-Basic
    """

    global CONN
    CONN = db.access_database(path)
    global ADDRESS
    ADDRESS = address
    global AUTH_HEADER
    AUTH_HEADER = auth_header
    asyncio.run(ble_function())


async def ble_function():
    """
    Reconnects all sensor_stations from database.db and starts polling for coupling.
    """

    connected_sensor_stations = db.get_all_sensorstations(CONN)
    for (dip, mac) in connected_sensor_stations:
        if rc.request_sensorstation_status(ADDRESS, AUTH_HEADER, dip)["deleted"]:
            db.remove_sensorstation_from_limits(CONN, dip)
            logger.log_info(f"Deleted Station with ID {dip}")
            continue
        TASKPOOL.apply(reconnect, args=[mac])
    await poll_for_coupling()
    await TASKPOOL.gather_and_close()


async def reconnect(mac: str):
    """
    Reconnect to device after restarting of AccessPoint.

    Arguments
    ----------
    mac: str
        MAC-Address of the Device

    """

    device = await search_for_device(mac)
    await call_services(device, True)


async def search_for_device(mac: str) -> BLEDevice:
    """
    Searches for a BLEDevice by MAC-Address.

    Arguments
    ----------
    mac: str
        MAC-Address of the Device.
    Returns
    ----------
    BLEDevice:
        Found device with MAC-Address
    """

    device = None
    attempt = 1
    while device is None or device.name is None:
        logger.log_info(f"Trying to find device with MAC {mac}. Try #{attempt}")
        device = await BleakScanner.find_device_by_address(mac)
        attempt += 1
    logger.log_info(f"Device with MAC {mac} found. Name: {device.name}")
    return device


async def poll_for_coupling():
    """
    Polls for Coupling Mode and starts scanning if Coupling Mode should be active.
    """

    while True:
        coupling = rci.request_couple_mode(ADDRESS, AUTH_HEADER)
        if coupling:
            await scan_for_devices()
        await asyncio.sleep(30)
        await TASKPOOL.flush()


async def scan_for_devices():
    """
    Starts BleakScanner and calls my_callback for each found BLEDevice.
    Stops scanning after 5 minutes or after polling mode is turned off on Webserver.
    """

    scanner = BleakScanner(detection_callback=my_callback)
    global SCANNED_DEVICES
    SCANNED_DEVICES = set()
    await scanner.start()
    logger.log_info("Started Coupling Mode")
    timeout = time.time() + 60 * 5
    while time.time() <= timeout:
        if not rci.request_couple_mode(ADDRESS, AUTH_HEADER):
            break
        await asyncio.sleep(10)
    if time.time() > timeout:
        logger.log_info("Coupling Mode timed out")
        rc.coupling_timed_out(ADDRESS, AUTH_HEADER)
    else:
        logger.log_info("Stopped Coupling Mode")
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

    if any(dip == get_dip_from_device(device) for (dip, _) in db.get_all_sensorstations(CONN)):
        return False
    if any(d.name == device.name for d in SCANNED_DEVICES):
        return False
    SCANNED_DEVICES.add(device)
    return True


async def establish_connection(device: BLEDevice):
    """
    Function for each SensorStation which polls for Verification and calls services.

    Arguments
    ----------
    device: BLEDevice
        SensorStation
    """

    verified = await poll_for_verification(device)
    if verified:
        TASKPOOL.apply(call_services, args=[device, False])


async def poll_for_verification(device: BLEDevice) -> bool:
    """
    Poll for the verification of this SensorStation on the Webserver.

    Arguments
    ----------
    device: BLEDevice
        SensorStation

    Returns
    -------
    bool:
        True if Device was verified within timelimit
    """

    verified = False
    dip = get_dip_from_device(device)
    available = rci.propose_new_sensorstation_at_server(ADDRESS, dip, device.address, AUTH_HEADER)
    if available is None or not available:
        return False
    timeout = time.time() + 60 * 5
    while not verified:
        verified = rci.request_sensorstation_if_verified(ADDRESS, dip, AUTH_HEADER)
        await asyncio.sleep(5)
        if time.time() > timeout:
            logger.log_info(f"Polling for Verification of {device.name} timed out")
            return False
    return verified


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

    # If device is already in database return dip which is saved in database
    # --> Can't plug out Station change DIP and then reconnect with different dip
    dips = [dip for (dip, mac) in db.get_all_sensorstations(CONN) if mac == device.address]
    if dips:
        return dips[0]
    return int(re.split(device_name_prefix, device.name)[1])


async def call_services(device: BLEDevice, already_connected: bool):
    """
    Uses the services of the BLEDevice to read Data and send limit excess.

    Arguments
    ----------
    device: BLEDevice
        SensorStation
    already_connected: bool
        Whether SensorStation is being reconnected

    """

    dip = get_dip_from_device(device)
    excesses = {"temp": 0,
                "pressure": 0,
                "humid": 0,
                "quality": 0,
                "soil": 0,
                "light": 0}
    active_excess = False
    # client == sensor_station
    client = BleakClient(device, timeout=30)
    # Try initial Connection
    if not already_connected:
        SCANNED_DEVICES.remove(device)
        try:
            await client.connect()
            rci.register_new_sensorstation_at_server(ADDRESS, dip, AUTH_HEADER)
            db.init_limits(CONN, dip, device.address)
            logger.log_info(f"Successfully established connection to {device.name}")
        except (asyncio.exceptions.CancelledError, asyncio.exceptions.TimeoutError):
            logger.log_error(f"Establishing BLE connection to {device.name} timed out")
            rci.connection_timed_out(ADDRESS, dip, AUTH_HEADER)
            return
    data = SensorData(dip, 0, 0, 0, 0, 0, 0)
    value = 0
    while True:
        time.sleep(10)
        sensor_station_status = rc.request_sensorstation_status(ADDRESS, AUTH_HEADER, dip)
        if sensor_station_status != {}:
            if sensor_station_status["deleted"]:
                break
            if not sensor_station_status["enabled"]:
                continue
        if not await ensure_connection(client, device):
            continue
        rc.refresh_connection_sensor_station(ADDRESS, AUTH_HEADER, dip)
        for data_type in DATA_UUIDS.keys():
            try:
                uuid = DATA_UUIDS[data_type]
                value = struct.unpack('d', await client.read_gatt_char(uuid))[0]
                data.set_value(data_type, value)
            except Exception as e:
                logger.log_error(f"Couldn't read characteristic {data_type}: {e}")
            if not active_excess:
                lower, upper = db.get_limits(CONN, dip, data_type)
                excess = db.calculate_limit(value, lower, upper)

                if abs(excess - 1.0) > 0.001:
                    try:
                        gardener_is_here = struct.unpack('?', await client.read_gatt_char(GARDENER_UUID))[0]
                        if gardener_is_here:
                            continue
                    except Exception as e:
                        logger.log_error(f"Couldn't read gardener characteristic: {e}")
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
                            logger.log_error(f"Couldn't read limit characteristic of {data_type}: {e}")
                else:
                    excesses[data_type] = 0
        if active_excess:
            try:
                gardener_is_here = struct.unpack('?', await client.read_gatt_char(GARDENER_UUID))[0]
                if gardener_is_here:
                    active_excess = False
                    rc.gardener_is_at_station(ADDRESS, dip, AUTH_HEADER)
            except Exception as e:
                logger.log_error(f"Couldn't read gardener characteristic: {e}")
        db.insert_sensor_data(CONN, data)
    if await ensure_connection(client, device):
        try:
            await client.disconnect()
            logger.log_info(f"Safely disconnected from {device.name}.")
        except Exception as e:
            logger.log_error(f"Couldn't disconnect from {device.name}: {e}")
    db.remove_sensorstation_from_limits(CONN, dip)
    logger.log_info(f"Deleted {device.name}")


async def ensure_connection(client: BleakClient, device: BLEDevice) -> bool:
    """
    Checks if device is connected and if not tries to reestablish connection.

    Arguments
    ---------
    client: BleClient
        BLEClient object of sensor_station
    device: BLEDevice
        BLEDevice object of sensor_station

    Returns
    ---------
    bool:
        True if device is connected after function call
    """

    # if sensor_station loses power, client.is_connected is still true
    try:
        await client.read_gatt_char(DATA_UUIDS["temp"])
    except Exception:
        logger.log_error(f"No connection to {device.name} possible")
    if not client.is_connected:
        logger.log_info(f"Trying to reconnect to {device.name}")
        try:
            await client.connect()
            logger.log_info(f"Successfully reconnected to {device.name}")
        except (asyncio.exceptions.CancelledError, asyncio.exceptions.TimeoutError,
                bleak.exc.BleakDeviceNotFoundError):
            logger.log_error(f"Can't reconnect to {device.name}")
            return False
    return True
