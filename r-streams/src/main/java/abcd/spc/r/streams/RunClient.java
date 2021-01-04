package abcd.spc.r.streams;

import io.micronaut.configuration.kafka.annotation.*;
import io.micronaut.messaging.annotation.Body;

@KafkaClient
public interface RunClient {
    @Topic("runs")
    void sendRun(@KafkaKey String key, @Body Run run);
}
