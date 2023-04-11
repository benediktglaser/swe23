import configparser

config = configparser.ConfigParser()
config.read_file(open(r'/home/benedikt/Documents/conf.yaml'))
address = config.get('config', 'address')
interval = config.get('config', 'interval')
name = config.get('config', 'name')

print(address)
print(interval)
print(name)