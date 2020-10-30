package abcd.spc.l.streams.sr;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public abstract class BaseSyncControlConsumerLoop<K, V> implements Runnable {
    KafkaConsumer<K, V> consumer;
    String id;
    String appId;
    String clientId;
    String groupId;
    String bootstrapServers;
    List<String> inputTopics;

    public BaseSyncControlConsumerLoop(
            String id,
            String appId,
            String clientId,
            String groupId,
            String bootstrapServers,
            List<String> inputTopics
    ) {
        this.id = id;
        this.appId = appId;
        this.clientId = clientId;
        this.groupId = groupId;
        this.bootstrapServers = bootstrapServers;
        this.inputTopics = inputTopics;
    }

    @Override
    public void run() {
        if (consumer != null) {
            return;
        }
        consumer = createConsumer();
        try {
            consumer.subscribe(inputTopics);
            while (true) {
                ConsumerRecords<K, V> consumerRecords = consumer.poll(Duration.ofSeconds(5));
                calculate(consumerRecords);
                consumer.commitSync();
            }
        } catch (Exception e) {

        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        if (consumer != null) {
            consumer.wakeup();
        }
    }

    protected KafkaConsumer<K, V> createConsumer() {
        return new KafkaConsumer<>(createProperties());
    }

    protected Properties createProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer());
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    protected abstract String keyDeserializer();

    protected abstract String valueDeserializer();

    protected abstract void calculate(ConsumerRecords<K, V> consumerRecords);
}
