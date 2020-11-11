import asyncio
import websockets
import uuid

uri = "ws://localhost:8000/send/41"

topic = "hello"
k = str(uuid.uuid4())
v = str(uuid.uuid4())
value = f"{topic}:{k}:{v}"


async def run():
    async with websockets.connect(uri) as websocket:
        await websocket.send(value)
        print(f"> {value}")
        greeting = await websocket.recv()
        print(f"< {greeting}")


asyncio.get_event_loop().run_until_complete(run())
