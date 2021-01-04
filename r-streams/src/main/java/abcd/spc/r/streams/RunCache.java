package abcd.spc.r.streams;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class RunCache {
    Map<String, RunStatus> statuses = new ConcurrentHashMap<>();

    Map<String, String> responses = new ConcurrentHashMap<>();
}
