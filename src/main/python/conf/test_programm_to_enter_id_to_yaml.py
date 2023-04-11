import yaml


def write_to_yaml(name: str, password: str) -> None:
    credentials = {"name": name, "password": password}

    try:
        with open(r"identification.yaml", "w+") as file:
            docs = yaml.dump(credentials, file)
    except:
        print("e")


def read_from_yaml():

    with open(r"identification.yaml", "r") as file:
        data = list(yaml.load_all(file, Loader=yaml.FullLoader))
    return (data[0]["name"], data[0]["password"])


if __name__ == "__main__":
    write_to_yaml("hund", "topSecret")
    print(read_from_yaml())
