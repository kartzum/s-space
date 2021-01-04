package abcd.spc.r.streams;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.UUID;

@Controller("/runs")
public class RunController {
    @Inject
    RunClient runClient;

    @Inject
    RunCache runCache;

    @Post
    public String runs(@Body String body) {
        String key = UUID.randomUUID().toString();
        runCache.statuses.put(key, RunStatus.RUNNING);
        runCache.responses.put(key, "");
        runClient.sendRun(key, new Run(key, RunType.REQUEST, "", body));
        return key;
    }

    @Get("/{key}/status")
    public Maybe<RunStatus> getRunStatus(String key) {
        return Maybe.just(key)
                .subscribeOn(Schedulers.io())
                .map(it -> runCache.statuses.getOrDefault(it, RunStatus.UNKNOWN));
    }

    @Get("/{key}")
    public Maybe<String> getRunResponse(String key) {
        return Maybe.just(key)
                .subscribeOn(Schedulers.io())
                .map(it -> runCache.responses.getOrDefault(it, ""));
    }
}
