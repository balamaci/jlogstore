package ro.balamaci.jlogstore.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple Publisher that just outputs to log
 */
public class SimpleLogPublisher implements Publisher {

    private static final Logger log = LoggerFactory.getLogger(SimpleLogPublisher.class);

    @Override
    public void publish(String logId, String json) {
        log.info("Publishing {}", json);
    }
}
