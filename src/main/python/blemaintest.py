# This file is only used for testing manually if ble works

import asyncio
import myble


async def main():
    await myble.establish_connection()


if __name__ == '__main__':
    asyncio.run(main())
