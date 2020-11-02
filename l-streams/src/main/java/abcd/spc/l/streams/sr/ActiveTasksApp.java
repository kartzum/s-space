package abcd.spc.l.streams.sr;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
            int activeTasksN = 1;
            ExecutorService activeTasksExecutor = Executors.newFixedThreadPool(activeTasksN);
            List<ActiveTasksLoop> activeTasksConsumers = new ArrayList<>();
            for (int i = 0; i < activeTasksN; i++) {
                ActiveTasksLoop activeTasksLoop = new ActiveTasksLoop(
                        Integer.toString(i),
                        "active_tasks",
                        "active_tasks",
                        "active_tasks",
                        bootstrapServers,
                        Collections.singletonList(activeTasksTopic)
                );
                activeTasksConsumers.add(activeTasksLoop);
                activeTasksExecutor.submit(activeTasksLoop);
            }
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    for (ActiveTasksLoop consumer : activeTasksConsumers) {
                        consumer.close();
                    }
                    activeTasksExecutor.shutdown();
                    try {
                        activeTasksExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                    }
                }
            });
        }

        static class ActiveTasksLoop implements Runnable, AutoCloseable {
            String id;
            String appId;
            String clientId;
            String groupId;
            String bootstrapServers;
            List<String> inputTopics;

            KafkaConsumer<String, String> consumer;

            ActiveTasksLoop(
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
                consumer = new KafkaConsumer<>(createProperties());
                consumer.subscribe(inputTopics);
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                    for (ConsumerRecord<String, String> record : records) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("key", record.key());
                        data.put("value", record.value());
                        data.put("partition", record.partition());
                        data.put("offset", record.offset());
                        System.out.println(data);
                    }
                }
            }

            Properties createProperties() {
                Properties consumerProperties = new Properties();
                consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
                consumerProperties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
                consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
                consumerProperties.setProperty(
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
                consumerProperties.setProperty(
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
                consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                return consumerProperties;
            }

            @Override
            public void close() {
                if (consumer != null) {
                    consumer.wakeup();
                }
            }
        }

        @Override
        public void close() {
        }
    }
}
