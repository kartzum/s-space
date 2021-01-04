package abcd.spc.r.streams;

import io.micronaut.configuration.kafka.annotation.*;
import io.micronaut.messaging.annotation.Body;

import javax.inject.Inject;

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
public class RunListener {
    @Inject
    RunCalculator runCalculator;

    @Topic("runs")
    public void receive(@KafkaKey String key, @Body Run run) {
        runCalculator.run(key, run);
    }
}
