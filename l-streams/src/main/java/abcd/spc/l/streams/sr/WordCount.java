package abcd.spc.l.streams.sr;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Word Count.
 * https://github.com/confluentinc/kafka-streams-examples/blob/6.0.0-post/src/main/java/io/confluent/examples/streams/WordCountLambdaExample.java
 *
 * <pre>
 * {@code
 * ./bin/zookeeper-server-start.sh /data/kafka/kafka_2.12-2.3.0-1/config/zookeeper.properties
 * ./bin/kafka-server-start.sh /data/kafka/kafka_2.12-2.3.0-1/config/server.properties
 *
 * ./bin/kafka-topics.sh --create --topic q-data --replication-factor 1 --partitions 1 --zookeeper localhost:2181
 * ./bin/kafka-topics.sh --create --topic w-data --replication-factor 1 --partitions 1 --zookeeper localhost:2181
 *
 * ./bin/kafka-topics.sh --list --zookeeper localhost:2181
 *
 * ./bin/kafka-console-consumer.sh --topic w-data --from-beginning --bootstrap-server localhost:9092 --property print.key=true --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
 * ./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic q-data
 *
 * >a b c
 * >c d
 *
 * a       1
 * b       1
 * c       1
 * c       2
 * d       1
 *
 * }
 * </pre>
 */
public class WordCount {
    public static void main(String[] args) {
        run(args);
    }

    static void run(String[] args) {
        String bootstrapServers = "localhost:9092";
        String inputTopic = "q-data";
        String outputTopic = "w-data";
        String applId = "wc_app";
        String clientId = "wc_client";

        if (args.length > 0) {
            bootstrapServers = args[0];
            inputTopic = args[1];
            outputTopic = args[2];
        }

        final Properties streamsConfiguration = getStreamsConfiguration(applId, clientId, bootstrapServers);

        final StreamsBuilder builder = new StreamsBuilder();
        createWordCountStream(inputTopic, outputTopic, builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    static Properties getStreamsConfiguration(
            final String appId, final String clientId, final String bootstrapServers) {
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, clientId);
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        return streamsConfiguration;
    }

    static void createWordCountStream(
            final String inputTopic, final String outputTopic, final StreamsBuilder builder) {
        final KStream<String, String> textLines = builder.stream(inputTopic);
        final Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
        final KTable<String, Long> wordCounts = textLines
                .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
                .groupBy((keyIgnored, word) -> word)
                .count();
        wordCounts.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
    }
}
