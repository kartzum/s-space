package abcd.spc.r.streams;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;

import org.junit.jupiter.api.Test;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

public class RunTcTest extends RunBase {

    @Test
    public void test() {
        try (KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.5.3"))) {
            kafka.start();
            Map<String, Object> properties = new HashMap<>();
            properties.put("kafka.bootstrap.servers", kafka.getBootstrapServers());
            try (EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties)) {
                ApplicationContext applicationContext = embeddedServer.getApplicationContext();
                HttpClient client = applicationContext.createBean(HttpClient.class, embeddedServer.getURI());

                run(client);
            }
        }
    }
}
