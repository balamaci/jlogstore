package ro.balamaci.jlogstore.publisher;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.HdrHistogram.Histogram;

/**
 *
 */
public class TimestampMeasuringPublisher implements Publisher {

    private ObjectMapper mapper = new ObjectMapper();
    private JsonFactory factory = mapper.getFactory();

    private final String timestampMillisNodeName;

    private Histogram histogram = new Histogram(0);

    private int eventsProcessed;

    private int numberOfEventsToShowHistogramStats;

    private long maxVal = 0;


    public TimestampMeasuringPublisher(String timestampMillisNodeName, int numberOfEventsToShowHistogramStats) {
        this.timestampMillisNodeName = timestampMillisNodeName;
        this.numberOfEventsToShowHistogramStats = numberOfEventsToShowHistogramStats;
    }

    @Override
    public void publish(String logId, String jsonString) {
        try {
            long now = System.currentTimeMillis();
            eventsProcessed ++;

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
                System.out.println("** Max=" + maxVal + " " + jsonString);
            }
            System.out.println("crt=" + timeDifference + " Max=" + maxVal + " " + jsonString);

            histogram.recordValue(timeDifference);
            if(eventsProcessed % numberOfEventsToShowHistogramStats == 0) {
                histogram.outputPercentileDistribution(System.out, 1.0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
