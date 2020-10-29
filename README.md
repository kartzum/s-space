# s-space
Services and examples

## l-streams
Applications using Apache Kafka.

```
./bin/zookeeper-server-start.sh /data/kafka/kafka_2.12-2.3.0-1/config/zookeeper.properties
./bin/kafka-server-start.sh /data/kafka/kafka_2.12-2.3.0-1/config/server.properties

./bin/kafka-topics.sh --create --topic q-data --replication-factor 1 --partitions 1 --zookeeper localhost:2181

./bin/kafka-topics.sh --list --zookeeper localhost:2181

./bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic q-data
```

### WordCount
Word Count example.

### ScConsumer
Sync control Consumer.

### SpProducer.
Simple Producer.

