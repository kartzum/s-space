package abcd.spc.l.streams.sr;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Sync control Consumer.
 * https://docs.cloudera.com/HDPDocuments/HDP2/HDP-2.6.0/bk_kafka-component-guide/content/ch_kafka-development.html
 * https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-9-consumer-client/
 *
 * <pre>
 * {@code
 * ./bin/kafka-console-producer.sh --topic q-data --broker-list localhost:9092 --property key.separator=":" --property "parse.key=true"
 *
 * >12:abc
 * >123:aaa
 *
 * 0: {partition=0, offset=4, value=abc, key=12}
 * 0: {partition=0, offset=5, value=aaa, key=123}
 *
 * }
 * </pre>
 */
public class ScConsumer {
    public static void main(String[] args) {
        run(args);
    }

    static void run(String[] args) {
        String bootstrapServers = "localhost:9092";
        String inputTopic = "q-data";
        String outputTopic = "w-data";
        String applId = "as_con_1";
        String clientId = "as_cons_1";
        String groupId = "as_cons_1";
        int numConsumers = 1;

        if (args.length > 0) {
            bootstrapServers = args[0];
            inputTopic = args[1];
            outputTopic = args[2];
            applId = args[3];
            clientId = args[4];
            groupId = args[5];
            numConsumers = Integer.parseInt(args[6]);
        }

        execute(bootstrapServers, inputTopic, outputTopic, applId, clientId, groupId, numConsumers);
    }

    static void execute(
            String bootstrapServers,
            String inputTopic,
            String outputTopic,
            String applId,
            String clientId,
            String groupId,
            int numConsumers
    ) {
        List<String> inputTopics = Collections.singletonList(inputTopic);
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);
        List<BaseSyncControlConsumerLoop<String, String>> consumers = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
            BaseSyncControlConsumerLoop<String, String> consumer =
                    new ScConsumerLoop(
                            Integer.toString(i), applId, clientId, groupId, bootstrapServers, inputTopics);
            consumers.add(consumer);
            executor.submit(consumer);
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (BaseSyncControlConsumerLoop<String, String> consumer : consumers) {
                    consumer.shutdown();
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    static class ScConsumerLoop extends BaseStringStringSyncControlConsumerLoop {
        ScConsumerLoop(
                String id,
                String appId,
                String clientId,
                String groupId,
                String bootstrapServers,
                List<String> inputTopics
        ) {
            super(id, appId, clientId, groupId, bootstrapServers, inputTopics);
        }

        @Override
        protected void calculate(ConsumerRecords<String, String> consumerRecords) {
            for (ConsumerRecord<String, String> record : consumerRecords) {
                Map<String, Object> data = new HashMap<>();
                data.put("key", record.key());
                data.put("value", record.value());
                data.put("partition", record.partition());
                data.put("offset", record.offset());
                System.out.println(this.id + ": " + data);
            }
        }
    }
}
