import logging

logging.basicConfig(filename='log.log', encoding='utf-8', level=logging.DEBUG, format='%(asctime)s %(message)s')

def log_info(string:str):
    logging.info(str)

def log_debug(string:str):
    logging.debug(str)

def log_warning(string:str):
    logging.warning(str)

def log_error(string:str):
    logging.error(str)