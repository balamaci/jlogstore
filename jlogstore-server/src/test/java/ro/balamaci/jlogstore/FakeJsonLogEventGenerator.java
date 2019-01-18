package ro.balamaci.jlogstore;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class FakeJsonLogEventGenerator {

    private ObjectMapper objectMapper = new ObjectMapper();

    public List<String> generateJsonLogEvents(int events) {
        try {
            List<String> jsonEvents = new ArrayList<>();

            for(int i=0; i < events; i++) {
                LogEvent logEvent = new LogEvent();
                jsonEvents.add(objectMapper.writeValueAsString(logEvent));
            }
            return jsonEvents;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
