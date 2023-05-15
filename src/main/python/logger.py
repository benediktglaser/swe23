import logging

logging.basicConfig(
    filename="log.log",
    encoding="utf-8",
    level=logging.INFO,
    format="%(asctime)s  %(funcName)s  %(levelname)s %(message)s",
)


def log_info(string: str):
    """
    Writes to the log file with priority info
    Parameter:
    ----------
    string : str
        The string that should be written into the log
    """

    logging.info(string)


def log_debug(string: str):
    """
    Writes to the log file with priority debug
    Parameter:
    ----------
    string : str
        The string that should be written into the log
    """

    logging.debug(string)


def log_warning(string: str):
    """
    Writes to the log file with priority warning
    Parameter:
    ----------
    string : str
        The string that should be written into the log
    """

    logging.warning(string)


def log_error(string: str):
    """
    Writes to the log file with priority error
    Parameter:
    ----------
    string : str
        The string that should be written into the log
    """

    logging.error(string)
