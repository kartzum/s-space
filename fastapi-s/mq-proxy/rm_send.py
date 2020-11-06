#!/usr/bin/env python
import pika

host = "localhost"
port = 5672
topic = "hello"
key = "hello"
value = "Hello World!"

connection = pika.BlockingConnection(pika.ConnectionParameters(host=host, port=port))
channel = connection.channel()
channel.queue_declare(queue=topic)
channel.basic_publish(exchange="", routing_key=key, body=value)
connection.close()
