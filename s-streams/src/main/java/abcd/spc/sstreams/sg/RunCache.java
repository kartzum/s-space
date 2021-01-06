package abcd.spc.sstreams.sg;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RunCache {
    Map<String, RunStatus> statuses = new ConcurrentHashMap<>();

    Map<String, String> responses = new ConcurrentHashMap<>();
}
