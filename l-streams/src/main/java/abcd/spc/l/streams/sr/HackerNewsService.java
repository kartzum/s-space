package abcd.spc.l.streams.sr;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HackerNewsService {
    HttpService service = new HttpService();
    String baseUrl = "https://hacker-news.firebaseio.com/v0/";

    List<String> fetchTopStoriesIds() {
        List<String> result = new ArrayList<>();
        Optional<String> response = service.get(getTopStoriesUrl(), (c) -> {
            service.prepareConnectionForJson(c);
        });
        if (response.isPresent()) {
            JSONParser jsonParser = new JSONParser();
            try {
                JSONArray jsonArray = (JSONArray) jsonParser.parse(response.get());
                for (Object value : jsonArray) {
                    result.add(value.toString());
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    String getTopStoriesUrl() {
        return baseUrl + "topstories.json";
    }
}
