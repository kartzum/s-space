package abcd.spc.r.streams;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class RunBase {
    void run(HttpClient client) {
        String key = client.toBlocking().retrieve(HttpRequest.POST("/runs", "body"));
        RunStatus runStatus = RunStatus.UNKNOWN;
        while (runStatus != RunStatus.DONE) {
            runStatus = client.toBlocking().retrieve(HttpRequest.GET("/runs/" + key + "/status"), RunStatus.class);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String response = client.toBlocking().retrieve(HttpRequest.GET("/runs/" + key), String.class);
        assertEquals("body_calculated", response);
    }
}
