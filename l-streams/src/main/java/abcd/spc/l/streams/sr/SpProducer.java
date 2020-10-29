package abcd.spc.l.streams.sr;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;

import java.util.Properties;
import java.util.Random;

/**
 * Simple Producer.
 * <p>
 * https://kafka.apache.org/20/javadoc/org/apache/kafka/clients/producer/KafkaProducer.html
 * <pre>
 * {@code
 *  ./bin/kafka-console-consumer.sh --topic w-data --from-beginning --bootstrap-server localhost:9092 --property print.key=true --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
 * }
 * </pre>
 */
public class SpProducer {
    public static void main(String[] args) {
        run(args);
    }

    static void run(String[] args) {
        String bootstrapServers = "localhost:9092";
        String topic = "w-data";
        String key = Integer.toString(new Random().nextInt());
        String value = Integer.toString(new Random().nextInt());
        if (args.length > 0) {
            bootstrapServers = args[0];
            topic = args[1];
            key = args[2];
            value = args[3];
        }
        new OnceRecordProducer<String, String>(bootstrapServers, topic).send(key, value);
    }

    static class OnceRecordProducer<K, V> {
        final String bootstrapServers;
        final String topic;

        OnceRecordProducer(String bootstrapServers, String topic) {
            this.bootstrapServers = bootstrapServers;
            this.topic = topic;
        }

        void send(K key, V value) {
            try (Producer<K, V> producer = new KafkaProducer<>(createProperties())) {
                ProducerRecord<K, V> producerRecord = new ProducerRecord<>(topic, key, value);
                producer.send(producerRecord, (m, e) -> e.printStackTrace());
            } catch (KafkaException e) {
                e.printStackTrace();
            }
        }

        private Properties createProperties() {
            Properties properties = new Properties();
            properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            properties.put(ProducerConfig.ACKS_CONFIG, "all");
            properties.put(ProducerConfig.RETRIES_CONFIG, 1);
            properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
            properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
            properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 16384);
            return properties;
        }
    }
}
