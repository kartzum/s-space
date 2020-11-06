# s-space
Services and examples

## l-streams

### Applications using Apache Kafka

```
./bin/zookeeper-server-start.sh /data/kafka/kafka_2.12-2.3.0-1/config/zookeeper.properties
./bin/kafka-server-start.sh /data/kafka/kafka_2.12-2.3.0-1/config/server.properties

./bin/kafka-topics.sh --create --topic q-data --replication-factor 1 --partitions 1 --zookeeper localhost:2181

./bin/kafka-topics.sh --list --zookeeper localhost:2181

./bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic q-data
```

#### WordCount
Word Count example.

#### ScConsumer
Sync control Consumer.

#### SpProducer
Simple Producer.

#### ActiveTasksApp
Active Tasks App.

#### TodoApp
Todo App.

### Jetty
[Jetty](https://www.eclipse.org/jetty/documentation/current/index.html)

#### SimplestServer
[embedding-jetty](https://www.eclipse.org/jetty/documentation/current/embedding-jetty.html)

Simplest Server.

## fastapi-s
[fastapi](https://fastapi.tiangolo.com/)

```
conda install -c conda-forge fastapi
conda install -c conda-forge uvicorn
```

#### first-steps
[first-steps](https://fastapi.tiangolo.com/tutorial/first-steps/)

### templates
[templates](https://fastapi.tiangolo.com/advanced/templates/)

### websockets
[websockets](https://fastapi.tiangolo.com/advanced/websockets/)

### mq-proxy
[websockets](https://websockets.readthedocs.io/en/stable/index.html)
[rabbitmq-python](https://www.rabbitmq.com/tutorials/tutorial-one-python.html)
[handling-disconnections-and-multiple-clients](https://fastapi.tiangolo.com/advanced/websockets/#handling-disconnections-and-multiple-clients)

```
https://www.rabbitmq.com/install-generic-unix.html
brew install erlang
```

```
conda install -c conda-forge pika
```

```
sbin/rabbitmq-server
```