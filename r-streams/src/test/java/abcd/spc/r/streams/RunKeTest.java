package abcd.spc.r.streams;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class RunKeTest extends RunBase {
    @Test
    void test() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("kafka.bootstrap.servers", "localhost:9092");
        properties.put("kafka.embedded.enabled", "true");
        try (EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties)) {
            ApplicationContext applicationContext = embeddedServer.getApplicationContext();
            HttpClient client = applicationContext.createBean(HttpClient.class, embeddedServer.getURI());

            run(client);
        }
    }
}
