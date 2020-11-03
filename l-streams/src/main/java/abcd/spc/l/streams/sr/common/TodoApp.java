package abcd.spc.l.streams.sr.common;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TodoApp.
 *
 * <pre>
 * {@code
 * http://localhost:8090/todos/list
 * }
 * </pre>
 * <pre>
 * {@code
 * fetch('http://localhost:8090/todos/create', {
 *       method: 'post',
 *       body: '{"name": "name_1", "description": "description_1"}'
 *     }).then(function(response) {
 *       return response.json();
 *     }).then(function(data) {
 *       console.log(data);
 *     });
 * }
 * </pre>
 * <pre>
 * {@code
 * fetch('http://localhost:8090/todos/update', {
 *       method: 'put',
 *       body: '{"id": "d17b376b-a29b-4b73-90fe-4c4d019a5d99", "status": "DONE"}'
 *     }).then(function(response) {
 *       return response.json();
 *     }).then(function(data) {
 *       log(data);
 *     });
 * }
 * </pre>
 * <pre>
 * {@code
 * fetch('http://localhost:8090/todos/delete', {
 *       method: 'delete',
 *       body: '{"id": "d17b376b-a29b-4b73-90fe-4c4d019a5d99"}'
 *     }).then(function(response) {
 *       return response.json();
 *     }).then(function(data) {
 *       log(data);
 *     });
 * }
 * </pre>
 */
public class TodoApp {
    public static void main(String[] args) {
        todoServiceStart(args);
    }

    static void todoServiceStart(String[] args) {
        new TodoService().start();
    }

    static class TodoService implements AutoCloseable {
        HttpServer server;
        ExecutorService executor;
        int nThreads = 10;
        String hostName = "localhost";
        int hostPort = 8090;
        Map<String, Todo> todos;

        public void start() {
            try {
                todos = new ConcurrentHashMap<>();
                executor = Executors.newFixedThreadPool(nThreads);
                server = HttpServer.create(new InetSocketAddress(hostName, hostPort), 0);
                server.setExecutor(executor);
                server.createContext("/todos/list", new TodosHttpHandler(todos));
                server.createContext("/todos/create", new CreateTodoHttpHandler(todos));
                server.createContext("/todos/update", new UpdateTodoHttpHandler(todos));
                server.createContext("/todos/delete", new DeleteTodoHttpHandler(todos));
                server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {
            server.stop(0);
            executor.shutdownNow();
        }

        static class TodosHttpHandler implements HttpHandler {
            Map<String, Todo> todos;

            TodosHttpHandler(Map<String, Todo> todos) {
                this.todos = todos;
            }

            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if ("GET".equals(httpExchange.getRequestMethod())) {
                    try (OutputStream outputStream = httpExchange.getResponseBody()) {
                        String response = "[" + JSONObject.toJSONString(todos) + "]";
                        httpExchange.sendResponseHeaders(200, response.length());
                        outputStream.write(response.getBytes());
                        outputStream.flush();
                    }
                }
            }
        }

        static class CreateTodoHttpHandler implements HttpHandler {
            Map<String, Todo> todos;

            CreateTodoHttpHandler(Map<String, Todo> todos) {
                this.todos = todos;
            }

            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if ("POST".equals(httpExchange.getRequestMethod())) {
                    JSONObject jsonObject = readJsonObject(httpExchange);
                    if (jsonObject != null) {
                        String id = UUID.randomUUID().toString();
                        String name = jsonObject.get("name").toString();
                        String description = jsonObject.get("description").toString();
                        Todo todo = new Todo(id, name, description, "NEW");
                        todos.put(id, todo);
                        createOkResponse(httpExchange, id);
                    }
                }
            }
        }

        static class UpdateTodoHttpHandler implements HttpHandler {
            Map<String, Todo> todos;

            UpdateTodoHttpHandler(Map<String, Todo> todos) {
                this.todos = todos;
            }

            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if ("PUT".equals(httpExchange.getRequestMethod())) {
                    JSONObject jsonObject = readJsonObject(httpExchange);
                    if (jsonObject != null) {
                        String id = jsonObject.get("id").toString();
                        String status = jsonObject.get("status").toString();
                        Todo todo = todos.get(id);
                        Todo newTodo = new Todo(todo.id, todo.name, todo.description, status);
                        todos.replace(id, newTodo);
                        createOkResponse(httpExchange, id);
                    }
                }
            }
        }

        static class DeleteTodoHttpHandler implements HttpHandler {
            Map<String, Todo> todos;

            DeleteTodoHttpHandler(Map<String, Todo> todos) {
                this.todos = todos;
            }

            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if ("DELETE".equals(httpExchange.getRequestMethod())) {
                    JSONObject jsonObject = readJsonObject(httpExchange);
                    if (jsonObject != null) {
                        String id = jsonObject.get("id").toString();
                        todos.remove(id);
                        createOkResponse(httpExchange, id);
                    }
                }
            }
        }

        static class Todo {
            String id;
            String name;
            String description;
            String status;

            Todo() {
            }

            Todo(
                    String id,
                    String name,
                    String description,
                    String status
            ) {
                this.id = id;
                this.name = name;
                this.description = description;
                this.status = status;
            }

            @Override
            public String toString() {
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("name", name);
                map.put("description", description);
                map.put("status", status);
                return JSONObject.toJSONString(map);
            }
        }

        static JSONObject readJsonObject(HttpExchange httpExchange) {
            StringBuilder inputBuffer = new StringBuilder();
            try {
                try (InputStream inputStream = httpExchange.getRequestBody()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        inputBuffer.append(line.trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = null;
            if (inputBuffer.length() > 0) {
                try {
                    Object temp = jsonParser.parse(inputBuffer.toString());
                    jsonObject = (JSONObject) temp;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return jsonObject;
        }

        static void createOkResponse(HttpExchange httpExchange, String id) {
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            try {
                try (OutputStream outputStream = httpExchange.getResponseBody()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("id", id);
                    String response = JSONObject.toJSONString(m);
                    httpExchange.sendResponseHeaders(200, response.length());
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
