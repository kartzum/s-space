from typing import List

from fastapi import FastAPI, WebSocket, WebSocketDisconnect

app = FastAPI()


class ConnectionManager:
    def __init__(self):
        self.active_connections: List[WebSocket] = []

    async def connect(self, websocket: WebSocket):
        await websocket.accept()
        self.active_connections.append(websocket)

    def disconnect(self, websocket: WebSocket):
        self.active_connections.remove(websocket)

    async def send_personal_message(self, message: str, websocket: WebSocket):
        await websocket.send_text(message)

    async def broadcast(self, message: str):
        for connection in self.active_connections:
            await connection.send_text(message)


manager = ConnectionManager()

host = "localhost"
port = 5672


def rm_send(topic, key, value):
    import pika
    connection = pika.BlockingConnection(pika.ConnectionParameters(host=host, port=port))
    channel = connection.channel()
    channel.queue_declare(queue=topic)
    channel.basic_publish(exchange="", routing_key=topic, body=value)
    connection.close()
    return "ok"


def send(topic, key, value):
    return rm_send(topic, key, value)


@app.websocket("/send/{client_id}")
async def websocket_endpoint(websocket: WebSocket, client_id: int):
    await manager.connect(websocket)
    try:
        while True:
            data = await websocket.receive_text()
            data_array = data.split(":")
            topic = data_array[0]
            key = data_array[1]
            value = data_array[2]
            r = send(topic, key, value)
            t = data + "," + r
            await manager.send_personal_message(f"Data: {t}", websocket)
            await manager.broadcast(f"Client #{client_id} says: {t}")
    except WebSocketDisconnect:
        manager.disconnect(websocket)
        await manager.broadcast(f"Client #{client_id} left the chat")
