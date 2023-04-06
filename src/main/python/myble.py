import asyncio
from bleak import BleakScanner, BLEDevice, BleakClient
from bleak.backends.scanner import AdvertisementData
from dbconnection import insert_sensor_data

device_name_prefix = "SensorStation G1T2"
# all already connected SensorStations
DEVICES = []
# mutex on DEVICES
lock = asyncio.Lock()


# TODO: check if mutex is needed (and if, how to use it in filter_func)
# Filtering for connection
# Only connect to devices named "SensorStation G1T2 [X][X]X" - XXX being the DIP-Id.
# DIP-Id may not be duplicate
def filter_func(device: BLEDevice, advertisement: AdvertisementData) -> bool:
    if advertisement.local_name is None or not advertisement.local_name.startswith(device_name_prefix):
        return False
    # await lock.acquire()
    # print("Acquired Lock")
    if any(d.name == advertisement.local_name for d in DEVICES):
        # lock.release()
        return False
    # lock.release()
    # print("Released Lock")
    return True


async def establish_connection():
    device = await BleakScanner.find_device_by_filter(filter_func, 60)  # could also have timeout
    # device = await BleakScanner.find_device_by_name("HUAWEI P30", 60)
    if device is None:
        print("ERROR: Could not find a device")
        return

    await lock.acquire()
    DEVICES.append(device)
    lock.release()

    # client == sensor_station
    async with BleakClient(device) as client:
        print("Connected to device {0}".format(device.name))
        await call_services(client)
        print("INFO: Disconnecting from device {0} ...".format(device.name))
    print("INFO: Disconnected from device {0}".format(device.name))


async def call_services(client):
    print("Inside call_services()")
    await asyncio.sleep(3)
