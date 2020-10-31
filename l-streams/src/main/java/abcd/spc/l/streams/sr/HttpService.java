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
        StringBuilder response = new StringBuilder();
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
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

    public void prepareConnectionForJson(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
    }
}
