import yaml
import logger


def write_to_yaml(name: str, password: str) -> None:
    """
    Write a name and password into the identification.yaml file
    Parameters
    ----------
    name : str
        The name
    password: str
        The password
    """

    credentials = {"name": name, "password": password}

    try:
        with open(r"identification.yaml", "w+") as file:
            docs = yaml.dump(credentials, file)
    except:
        logger.log_error("error writing to identification.yaml")


def read_from_yaml() -> (str, str):
    """
    Reads a name and password from the identification.yaml file
    Returns
    -------
    (name : str, password : str)
    """

    try:
        with open(r"identification.yaml", "r") as file:
            data = list(yaml.load_all(file, Loader=yaml.FullLoader))
            if len(data) == 0:
                return None
        return data[0]["name"], data[0]["password"]
    except Exception as e:
        print(e)
        logger.log_error("error reading to identification.yaml")
        raise e
