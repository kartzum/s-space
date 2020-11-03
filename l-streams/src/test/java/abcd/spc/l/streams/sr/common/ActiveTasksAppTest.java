package abcd.spc.l.streams.sr.common;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActiveTasksAppTest {
    @Test
    void test() throws InterruptedException {
        String brokerHost = "127.0.0.1";
        int brokerPort = 29092;
        String activeTasksTopic = "active_tasks";
        try (KafkaServerService kafkaServerService = new KafkaServerService(brokerHost, brokerPort)) {
            kafkaServerService.start();
            kafkaServerService.createTopic(activeTasksTopic);

            ActiveTasksApp.ActiveTasksService activeTasksService =
                    new ActiveTasksApp.ActiveTasksService(
                            brokerHost + ":" + brokerPort, activeTasksTopic);
            activeTasksService.run();

            Map<String, Boolean> operations = new ConcurrentHashMap<>();

            String operationKey = ActiveTasksApp.generateKey();

            ActiveTasksApp.activeTasksSend(
                    brokerHost + ":" + brokerPort,
                    activeTasksTopic,
                    operationKey,
                    ActiveTasksApp.operationAdd(2, 2));

            KafkaConsumer<String, String> activeTasksConsumer = ActiveTasksApp.activeTasksCreateConsumer(
                    brokerHost + ":" + brokerPort, "active_tasks_2", "active_tasks_2");
            activeTasksConsumer.subscribe(Collections.singleton(activeTasksTopic));

            Executors.newFixedThreadPool(1).submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        ConsumerRecords<String, String> records =
                                activeTasksConsumer.poll(Duration.ofSeconds(1));
                        if (!records.isEmpty()) {
                            for (ConsumerRecord<String, String> record : records) {
                                String value = record.value();
                                org.json.simple.parser.JSONParser jsonParser = new JSONParser();
                                Object jsonObjectObject = null;
                                try {
                                    jsonObjectObject = jsonParser.parse(value);
                                } catch (ParseException e) {
                                }
                                if (jsonObjectObject != null) {
                                    JSONObject jsonObject = (JSONObject) jsonObjectObject;
                                    if (jsonObject.containsKey("type")) {
                                        String type = jsonObject.get("type").toString();
                                        if ("operation_result".equals(type)) {
                                            double result = Double.parseDouble(jsonObject.get("result").toString());
                                            operations.put("operation_1", result == 4);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });

            Thread.sleep(6000);

            assertTrue(operations.containsKey("operation_1"));
            assertTrue(operations.get("operation_1"));

            activeTasksService.close();
        }
    }
}
