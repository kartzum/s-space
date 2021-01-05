package abcd.spc.tc.streams;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collection;

import static abcd.spc.tc.streams.Utils.*;

@MicronautTest
public class ProducerConsumerTest {

    @Test
    public void testProducerConsumer() throws Exception {
        try (KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.5.3"))) {
            kafka.start();
            try (Consumer consumer = new Consumer(kafka.getBootstrapServers(), "topic")) {
                Utils.createTopic(kafka.getBootstrapServers(), "topic");
                produce(kafka.getBootstrapServers(), "topic", "key", "value");
                consumer.consume();
                Thread.sleep(2000);
                Collection<ConsumerRecord<String, String>> records = consumer.receivedRecords;
                Assertions.assertTrue(records.size() > 0);
            }
        }
    }
}
