package abcd.spc.r.streams;

import io.micronaut.configuration.kafka.embedded.KafkaEmbedded;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
@Property(name = "kafka.embedded.enabled", value = "true")
@Property(name = "kafka.bootstrap.servers", value = "localhost:9092")
public class RunTest {
    @Inject
    KafkaEmbedded kafkaEmbedded;

    @Inject
    EmbeddedServer server;

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    RunCache runCache;

    @Test
    void test() throws InterruptedException {
        String body = client.toBlocking().retrieve(HttpRequest.POST("/runs", "body"));
        Thread.sleep(5000);
        assertEquals("done", runCache.statuses.get(body));
        assertEquals("body_calculated", runCache.responses.get(body));
    }
}
