package ro.balamaci.jlogstore.publisher.es;

import ro.balamaci.jlogstore.publisher.PublishException;
import ro.balamaci.jlogstore.publisher.Publisher;

import java.io.IOException;
import java.util.List;

/**
 * @author sbalamaci
 */
public class ElasticSearchPublisher implements Publisher {

    private ElasticSearchGateway elasticSearchGateway;

    public ElasticSearchPublisher(List<String> hosts) {
        this.elasticSearchGateway = new ElasticSearchGateway(hosts);
    }

    @Override
    public void publish(String logId, String json) {
        try {
            elasticSearchGateway.index(logId, json);
        } catch (IOException e) {
            throw new PublishException(e);
        }
    }
}
