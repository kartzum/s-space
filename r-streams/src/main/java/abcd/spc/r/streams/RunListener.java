package abcd.spc.r.streams;

import io.micronaut.configuration.kafka.annotation.*;
import io.micronaut.messaging.annotation.Body;

import javax.inject.Inject;
import java.util.UUID;

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
public class RunListener {
    @Inject
    RunClient runClient;

    @Inject
    RunCache runCache;

    @Topic("runs")
    public void receive(@KafkaKey String key, @Body Run run) {
        if ("request".equals(run.getType())) {
            String runKey = run.getKey();
            String newKey = UUID.randomUUID().toString();
            String runBody = run.getBody();
            runClient.sendRun(newKey, new Run(newKey, "response", runKey, runBody + "_calculated"));
        } else if ("response".equals(run.getType())) {
            runCache.statuses.replace(run.getResponseKey(), "done");
            runCache.responses.replace(run.getResponseKey(), run.getBody());
        }
    }
}
