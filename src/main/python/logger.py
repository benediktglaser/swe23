import logging

logging.basicConfig(
    filename="log.log",
    encoding="utf-8",
    level=logging.DEBUG,
    format="%(asctime)s  %(funcName)s  %(levelname)s %(message)s",
)


def log_info(string: str):
    logging.info(string)


def log_debug(string: str):
    logging.debug(string)


def log_warning(string: str):
    logging.warning(string)


def log_error(string: str):
    logging.error(string)
