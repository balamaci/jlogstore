package ro.balamaci.jlogstore.publisher;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class TimestampMeasuringPublisher implements Publisher {

    private ObjectMapper mapper = new ObjectMapper();
    private JsonFactory factory = mapper.getFactory();

    private final String timestampMillisNodeName;

    private final Timer timed = StartPerf.metrics.timer("processedTime");

    private long maxVal = 0;


    public TimestampMeasuringPublisher(String timestampMillisNodeName) {
        this.timestampMillisNodeName = timestampMillisNodeName;
    }

    @Override
    public void publish(String logId, String jsonString) {
        try {
            long now = System.currentTimeMillis();

            JsonParser parser = factory.createParser(jsonString);
            JsonNode jsonNode = mapper.readTree(parser);
            jsonNode = jsonNode.get(timestampMillisNodeName);

            if(jsonNode == null) {
                return;
            }

            long eventTime = jsonNode.asLong();
            long timeDifference = now - eventTime;
            if(timeDifference > maxVal) {
                maxVal = timeDifference;
                System.out.println("Max=" + maxVal);
            }

//            processed.mark();
            timed.update(timeDifference, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
