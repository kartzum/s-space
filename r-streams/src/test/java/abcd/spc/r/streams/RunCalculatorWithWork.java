package abcd.spc.r.streams;

import io.micronaut.context.annotation.Replaces;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Replaces(RunCalculatorImpl.class)
@Singleton
public class RunCalculatorWithWork implements RunCalculator {
    @Inject
    RunClient runClient;

    @Inject
    RunCache runCache;

    @Override
    public void run(String key, Run run) {
        if (RunType.REQUEST.equals(run.getType())) {
            String runKey = run.getKey();
            String newKey = UUID.randomUUID().toString();
            String runBody = run.getBody();
            runClient.sendRun(newKey, new Run(newKey, RunType.RESPONSE, runKey, runBody + "_calculated"));
        } else if (RunType.RESPONSE.equals(run.getType())) {
            runCache.statuses.replace(run.getResponseKey(), RunStatus.DONE);
            runCache.responses.replace(run.getResponseKey(), run.getBody());
        }
    }
}
