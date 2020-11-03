package abcd.spc.l.streams.sr.common;

import java.util.List;

public class HackerNewsServiceExperiments {
    public static void main(String[] args) {
        fetchTopStoriesIds();
    }

    static void fetchTopStoriesIds() {
        HackerNewsService service = new HackerNewsService();
        List<String> topStoriesIds = service.fetchTopStoriesIds();
        topStoriesIds.forEach(System.out::println);
    }
}
