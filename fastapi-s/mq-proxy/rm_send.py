#!/usr/bin/env python
import pika
import uuid

host = "localhost"
port = 5672

topic = "hello"
k = str(uuid.uuid4())
v = str(uuid.uuid4())
value = f"{topic}:{k}:{v}"

connection = pika.BlockingConnection(pika.ConnectionParameters(host=host, port=port))
channel = connection.channel()
channel.queue_declare(queue=topic)
channel.basic_publish(exchange="", routing_key=topic, body=value)
connection.close()
