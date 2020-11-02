package abcd.spc.l.streams.sr;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
                        activeTasksTopic
                );
                activeTasksConsumers.add(activeTasksLoop);
                activeTasksExecutor.submit(activeTasksLoop);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                for (ActiveTasksLoop consumer : activeTasksConsumers) {
                    consumer.close();
                }
                activeTasksExecutor.shutdown();
                try {
                    activeTasksExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                }
            }));
        }

        static class ActiveTasksLoop implements Runnable, AutoCloseable {
            String id;
            String appId;
            String clientId;
            String groupId;
            String bootstrapServers;
            String inputTopic;

            KafkaConsumer<String, String> consumer;

            ActiveTasksLoop(
                    String id,
                    String appId,
                    String clientId,
                    String groupId,
                    String bootstrapServers,
                    String inputTopic
            ) {
                this.id = id;
                this.appId = appId;
                this.clientId = clientId;
                this.groupId = groupId;
                this.bootstrapServers = bootstrapServers;
                this.inputTopic = inputTopic;
            }

            @Override
            public void run() {
                consumer = activeTasksCreateConsumer(bootstrapServers, clientId, groupId);
                consumer.subscribe(Collections.singleton(inputTopic));
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                    for (ConsumerRecord<String, String> record : records) {
                        org.json.simple.parser.JSONParser jsonParser = new JSONParser();
                        Object jsonObjectObject = null;
                        try {
                            jsonObjectObject = jsonParser.parse(record.value());
                        } catch (ParseException e) {
                        }
                        if (jsonObjectObject != null) {
                            JSONObject jsonObject = (JSONObject) jsonObjectObject;
                            calcRecord(record.key(), jsonObject);
                        }
                    }
                }
            }

            void calcRecord(String key, JSONObject jsonObject) {
                if (jsonObject.containsKey("type")) {
                    String type = jsonObject.get("type").toString();
                    switch (type) {
                        case "operation_add":
                            calcRecordAdd(key, jsonObject);
                            break;
                    }
                }
            }

            void calcRecordAdd(String key, JSONObject jsonObject) {
                double a = Double.parseDouble(jsonObject.get("a").toString());
                double b = Double.parseDouble(jsonObject.get("b").toString());
                double c = a + b;
                send(operationResult(key, Double.toString(c)));
            }

            void send(String value) {
                activeTasksSend(bootstrapServers, inputTopic, generateKey(), value);
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

    static String generateKey() {
        return UUID.randomUUID().toString();
    }

    static String operationAdd(double a, double b) {
        Map<String, String> map = new HashMap<>();
        map.put("type", "operation_add");
        map.put("a", Double.toString(a));
        map.put("b", Double.toString(b));
        return JSONObject.toJSONString(map);
    }

    static String operationResult(String responseKey, String result) {
        Map<String, String> map = new HashMap<>();
        map.put("response_id", responseKey);
        map.put("type", "operation_result");
        map.put("result", result);
        return JSONObject.toJSONString(map);
    }

    static void activeTasksSend(String bootstrapServers, String topic, String key, String value) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, String> data = new ProducerRecord<>(topic, key, value);
        producer.send(data);
        producer.flush();
        producer.close();
    }

    static KafkaConsumer<String, String> activeTasksCreateConsumer(String bootstrapServers, String clientId, String groupId) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(properties);
    }
}
