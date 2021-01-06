package abcd.spc.sstreams.sg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/runs")
public class RunController {
    @Autowired
    private RunClient runClient;

    @Autowired
    private RunCache runCache;

    @PostMapping()
    public String runs(@RequestBody String body) {
        String key = UUID.randomUUID().toString();
        runCache.statuses.put(key, RunStatus.RUNNING);
        runCache.responses.put(key, "");
        runClient.sendRun(key, new Run(key, RunType.REQUEST, "", body));
        return key;
    }

    @GetMapping("/{key}/status")
    public RunStatus getRunStatus(@PathVariable String key) {
        return runCache.statuses.getOrDefault(key, RunStatus.UNKNOWN);
    }

    @GetMapping("/{key}")
    public String getRunResponse(@PathVariable String key) {
        return runCache.responses.getOrDefault(key, "");
    }
}
