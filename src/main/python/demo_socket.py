import websocket
import json


def on_open(message):
    print("connection is established")

def on_message(ws, message):
    data = json.load(message)
    interval = data["sendingInterval"]
    print("Received interval: ", interval)
    #TODO write this interval to file

def on_error(ws, error):
    print("Error: ", error)


def on_close(ws):
    print("Connection is closed")

if __name__ == "__main__":

    ws = websocket.WebsocketApp("test",
on_open=on_open,
on_message=on_message,
on_error = on_error,
on_close = on_close)

ws.run_forever()
