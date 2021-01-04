package abcd.spc.r.streams;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RunCalculatorImpl implements RunCalculator {
    @Inject
    RunCache runCache;

    @Override
    public void run(String key, Run run) {
        if (RunType.RESPONSE.equals(run.getType())) {
            runCache.statuses.replace(run.getResponseKey(), RunStatus.DONE);
            runCache.responses.replace(run.getResponseKey(), run.getBody());
        }
    }
}
