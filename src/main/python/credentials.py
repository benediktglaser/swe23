import yaml


def write_to_yaml(name: str, password: str) -> None:
    credentials = {"name": name, "password": password}

    try:
        with open(r"identification.yaml", "w+") as file:        #TODO: change on raspberry
            docs = yaml.dump(credentials, file)
    except:
        print("e")


def read_from_yaml():
    try:
        with open(r"identification.yaml", "r") as file:         #TODO: change on raspberry
            data = list(yaml.load_all(file, Loader=yaml.FullLoader))
        return (data[0]["name"], data[0]["password"])
    except:
        return None

if __name__ == "__main__":
    print(read_from_yaml()[0])
