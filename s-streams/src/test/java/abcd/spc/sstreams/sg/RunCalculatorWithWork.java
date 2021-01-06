package abcd.spc.sstreams.sg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RunCalculatorWithWork implements RunCalculator {
    @Autowired
    RunClient runClient;

    @Autowired
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
