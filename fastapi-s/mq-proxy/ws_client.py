import asyncio
import websockets

uri = "ws://localhost:8000/ws/41"
message = "hello"


async def run():
    async with websockets.connect(uri) as websocket:
        await websocket.send(message)
        print(f"> {message}")
        greeting = await websocket.recv()
        print(f"< {greeting}")


asyncio.get_event_loop().run_until_complete(run())
