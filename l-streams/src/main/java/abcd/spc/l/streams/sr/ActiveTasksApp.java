package abcd.spc.l.streams.sr;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ActiveTasksApp {
    static class ActiveTasksService implements AutoCloseable {
        String bootstrapServers;
        String activeTasksTopic;

        ActiveTasksService(
                String bootstrapServers,
                String activeTasksTopic
        ) {
            this.bootstrapServers = bootstrapServers;
            this.activeTasksTopic = activeTasksTopic;
        }

        void run() {
            Properties consumerProps = new Properties();
            consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "active_tasks");
            consumerProps.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "active_tasks");
            consumerProps.setProperty(
                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            consumerProps.setProperty(
                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            KafkaConsumer<String, String> activeTasksConsumer = new KafkaConsumer<>(consumerProps);
            activeTasksConsumer.subscribe(Collections.singleton(activeTasksTopic));
            ConsumerRecords<String, String> records = activeTasksConsumer.poll(Duration.ofSeconds(5));
            for (ConsumerRecord<String, String> record : records) {
                Map<String, Object> data = new HashMap<>();
                data.put("key", record.key());
                data.put("value", record.value());
                data.put("partition", record.partition());
                data.put("offset", record.offset());
                System.out.println(data);
            }
        }

        @Override
        public void close() {
        }
    }
}
