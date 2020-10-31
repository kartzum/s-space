package abcd.spc.l.streams.sr;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class GitLabService {
    HttpService service = new HttpService();
    String baseUrl = "https://gitlab.com/api/v4/";
    String token;

    public GitLabService(String token) {
        this.token = token;
    }

    public List<Variable> getVariablesByProjectId(String projectId) {
        List<Variable> result = new ArrayList<>();
        String url = baseUrl + "projects/" + projectId + "/variables";
        Optional<String> response = service.get(url, (c) -> {
            service.prepareConnectionForJson(c);
            c.setRequestProperty("PRIVATE-TOKEN", token);
        });
        if (response.isPresent()) {
            JSONParser jsonParser = new JSONParser();
            try {
                JSONArray jsonArray = (JSONArray) jsonParser.parse(response.get());
                for (Object arrayItem : jsonArray) {
                    JSONObject jsonObject = (JSONObject) arrayItem;
                    Object key = jsonObject.get("key");
                    Object value = jsonObject.get("value");
                    if (key != null) {
                        String valueAsString = null;
                        if (value != null) {
                            valueAsString = value.toString();
                        }
                        Variable variable = new Variable(key.toString(), valueAsString);
                        result.add(variable);
                    }
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public void createVariable(String projectId, String variableKey, String variableValue) {
        String url = baseUrl + "projects/" + projectId + "/variables";
        Map<String, String> map = new HashMap<>();
        map.put("key", variableKey);
        map.put("value", variableValue);
        String data = JSONObject.toJSONString(map);
        Optional<String> response = service.post(url, (c) -> {
            try {
                service.prepareConnectionForJson(c);
                c.setRequestProperty("PRIVATE-TOKEN", token);
                c.setDoOutput(true);
                OutputStream os = c.getOutputStream();
                os.write(data.getBytes("UTF-8"));
                os.close();
            } catch (IOException e) {
            }
        });
    }

    static class Variable {
        String key;
        String value;

        Variable(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
