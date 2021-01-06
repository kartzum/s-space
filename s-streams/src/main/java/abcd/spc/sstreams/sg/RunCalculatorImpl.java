package abcd.spc.sstreams.sg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RunCalculatorImpl implements RunCalculator {
    @Autowired
    private RunCache runCache;

    @Override
    public void run(String key, Run run) {
        if (RunType.RESPONSE.equals(run.getType())) {
            runCache.statuses.replace(run.getResponseKey(), RunStatus.DONE);
            runCache.responses.replace(run.getResponseKey(), run.getBody());
        }
    }
}
