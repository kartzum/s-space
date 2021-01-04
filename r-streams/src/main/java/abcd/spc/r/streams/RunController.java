package abcd.spc.r.streams;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

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
        runCache.statuses.put(key, "running");
        runCache.responses.put(key, "");
        runClient.sendRun(key, new Run(key, "request", "", body));
        return key;
    }
}
