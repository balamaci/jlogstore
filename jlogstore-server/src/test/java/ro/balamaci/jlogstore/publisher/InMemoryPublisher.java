package ro.balamaci.jlogstore.publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPublisher implements IPublisher {

    private Map<String, List<String>> publishers = new HashMap<>();


    @Override
    public void publish(String logId, String json) {
        List<String> events = publishers.getOrDefault(logId, new ArrayList<>());
        events.add(json);
        publishers.put(logId, events);
    }

    public List<String> getPublished(String logId) {
        return publishers.get(logId);
    }

}
