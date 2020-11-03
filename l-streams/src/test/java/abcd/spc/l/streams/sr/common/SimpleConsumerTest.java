package abcd.spc.l.streams.sr.common;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleConsumerTest {
    @Test
    void test() {
        try (KafkaServerService service = new KafkaServerService("127.0.0.1", 19092)) {
            String topic = "topic";
            String message = "message";
            service.start();
            service.createTopic(topic);
            KafkaProducer<Integer, byte[]> producer = service.createKafkaProducerIntegerByteArray();
            KafkaConsumer<Integer, byte[]> consumer = service.createKafkaConsumerIntegerByteArray();
            consumer.subscribe(Collections.singleton(topic));
            ProducerRecord<Integer, byte[]> data =
                    new ProducerRecord<>(topic, 42, message.getBytes(StandardCharsets.UTF_8));
            producer.send(data);
            producer.close();
            ConsumerRecords<Integer, byte[]> records = consumer.poll(Duration.ofSeconds(5));
            assertEquals(1, records.count());
        }
    }
}
