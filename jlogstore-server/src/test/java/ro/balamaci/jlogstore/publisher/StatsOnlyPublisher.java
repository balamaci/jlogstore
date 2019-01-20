package ro.balamaci.jlogstore.publisher;

import java.util.HashMap;
import java.util.Map;

public class StatsOnlyPublisher implements Publisher {

    private Map<String, Integer> stats = new HashMap<>();

    @Override
    public void publish(String logId, String json) {
        Integer counter = stats.getOrDefault(logId, 0);
        counter++;

        stats.put(logId, counter);
    }
}
