package abcd.spc.l.streams.sr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class HttpService {
    public String get(String url, Consumer<HttpURLConnection> connectionHandler) {
        return op(url, "GET", connectionHandler);
    }

    public void post(String url, Consumer<HttpURLConnection> connectionHandler) {
        op(url, "POST", connectionHandler);
    }

    public void put(String url, Consumer<HttpURLConnection> connectionHandler) {
        op(url, "PUT", connectionHandler);
    }

    public void delete(String url, Consumer<HttpURLConnection> connectionHandler) {
        op(url, "DELETE", connectionHandler);
    }

    public void prepareConnectionForJson(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
    }

    String op(String url, String method, Consumer<HttpURLConnection> connectionHandler) {
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
            throw new RuntimeException(e);
        }
        return response.toString();
    }

}
