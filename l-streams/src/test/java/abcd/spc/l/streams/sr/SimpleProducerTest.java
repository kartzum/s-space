package abcd.spc.l.streams.sr;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.MockTime;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import kafka.zk.EmbeddedZookeeper;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import kafka.utils.TestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * https://gist.github.com/asmaier/6465468
 */
public class SimpleProducerTest {
    private static final String ZKHOST = "127.0.0.1";
    private static final String BROKERHOST = "127.0.0.1";
    private static final String BROKERPORT = "19092";
    private static final String TOPIC = "test";

    @Test
    void test() throws IOException {
        EmbeddedZookeeper zkServer = new EmbeddedZookeeper();
        String zkConnect = ZKHOST + ":" + zkServer.port();
        ZkClient zkClient = new ZkClient(
                zkConnect, 30000, 30000, ZKStringSerializer$.MODULE$);
        ZkUtils zkUtils = ZkUtils.apply(zkClient, false);

        Properties brokerProps = new Properties();
        brokerProps.setProperty("zookeeper.connect", zkConnect);
        brokerProps.setProperty("broker.id", "0");
        brokerProps.setProperty("log.dirs",
                Files.createTempDirectory("kafka-").toAbsolutePath().toString());
        brokerProps.setProperty("listeners", "PLAINTEXT://" + BROKERHOST + ":" + BROKERPORT);
        brokerProps.setProperty("offsets.topic.replication.factor", "1");
        KafkaConfig config = new KafkaConfig(brokerProps);
        MockTime mock = new MockTime();
        KafkaServer kafkaServer = TestUtils.createServer(config, mock);

        AdminUtils.createTopic(
                zkUtils, TOPIC, 1, 1, new Properties(), RackAwareMode.Disabled$.MODULE$);

        Properties producerProps = new Properties();
        producerProps.setProperty("bootstrap.servers", BROKERHOST + ":" + BROKERPORT);
        producerProps.setProperty("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        producerProps.setProperty("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        KafkaProducer<Integer, byte[]> producer = new KafkaProducer<Integer, byte[]>(producerProps);

        Properties consumerProps = new Properties();
        consumerProps.setProperty("bootstrap.servers", BROKERHOST + ":" + BROKERPORT);
        consumerProps.setProperty("group.id", "group0");
        consumerProps.setProperty("client.id", "consumer0");
        consumerProps.setProperty(
                "key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        consumerProps.setProperty(
                "value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumerProps.put("auto.offset.reset", "earliest");
        KafkaConsumer<Integer, byte[]> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Arrays.asList(TOPIC));

        ProducerRecord<Integer, byte[]> data =
                new ProducerRecord<>(TOPIC, 42, "test-message".getBytes(StandardCharsets.UTF_8));
        producer.send(data);
        producer.close();

        ConsumerRecords<Integer, byte[]> records = consumer.poll(5000);
        assertEquals(1, records.count());
        Iterator<ConsumerRecord<Integer, byte[]>> recordIterator = records.iterator();
        ConsumerRecord<Integer, byte[]> record = recordIterator.next();
        System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value());
        assertEquals(42, (int) record.key());
        assertEquals("test-message", new String(record.value(), StandardCharsets.UTF_8));

        kafkaServer.shutdown();
        zkClient.close();
        zkServer.shutdown();
    }
}
