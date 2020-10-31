package abcd.spc.l.streams.sr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;

public class HttpService {
    public Optional<String> get(String url, Consumer<HttpURLConnection> connectionHandler) {
        return op(url, "GET", connectionHandler);
    }

    public Optional<String> post(String url, Consumer<HttpURLConnection> connectionHandler) {
        return op(url, "POST", connectionHandler);
    }

    public Optional<String> put(String url, Consumer<HttpURLConnection> connectionHandler) {
        return op(url, "PUT", connectionHandler);
    }

    public void prepareConnectionForJson(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
    }

    Optional<String> op(String url, String method, Consumer<HttpURLConnection> connectionHandler) {
        StringBuilder response = new StringBuilder();
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod(method);
            connectionHandler.accept(connection);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String responseLine;
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
        } catch (IOException e) {
        }
        return Optional.of(response.toString());
    }

}
