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

    @Test
    void test() throws InterruptedException {
        String key = client.toBlocking().retrieve(HttpRequest.POST("/runs", "body"));
        RunStatus runStatus = RunStatus.UNKNOWN;
        while (runStatus != RunStatus.DONE) {
            runStatus = client.toBlocking().retrieve(HttpRequest.GET("/runs/" + key + "/status"), RunStatus.class);
            Thread.sleep(500);
        }
        String response = client.toBlocking().retrieve(HttpRequest.GET("/runs/" + key), String.class);
        assertEquals("body_calculated", response);
    }
}
