package abcd.spc.l.streams.sr;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GitLabService {
    HttpService service = new HttpService();
    String baseUrl = "https://gitlab.com/api/v4/";
    String token;

    public GitLabService(String token) {
        this.token = token;
    }

    public List<Variable> getVariablesByProjectId(String id) {
        List<Variable> result = new ArrayList<>();
        String url = baseUrl + "projects/" + id + "/variables";
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

    static class Variable {
        String key;
        String value;

        Variable(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
