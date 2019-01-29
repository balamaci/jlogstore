package ro.balamaci.jlogstore.publisher.es;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ElasticSearchGateway {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchGateway.class);

    private RestHighLevelClient restClient;

    public ElasticSearchGateway(List<String> hostsList) {
        HttpHost[] hosts = hostsList
                .stream()
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);

        restClient = new RestHighLevelClient(RestClient.builder(hosts));
    }

    boolean index(String index, String json) throws IOException  {
        log.info("Indexing to ES index={} source={}", index, json);

        IndexRequest indexRequest = new IndexRequest(index, "_doc", UUID.randomUUID().toString());
        indexRequest.source(json, XContentType.JSON);
        indexRequest.create(true);
        indexRequest.timeout(TimeValue.timeValueSeconds(1));

        try {
            IndexResponse indexResponse = restClient.index(indexRequest, RequestOptions.DEFAULT);
            return indexResponse.status() == RestStatus.CREATED;
        } catch (IOException e) {
            log.error("Error indexing", e);
            throw e;
        }
    }

    void putIndexTemplate(String templateName, String json) throws IOException  {
        log.info("Applying indexing template '{}'", templateName);
        PutIndexTemplateRequest putIndexTemplateRequest = new PutIndexTemplateRequest(templateName);
        putIndexTemplateRequest.source(json, XContentType.JSON);

        AcknowledgedResponse response = restClient.indices()
                .putTemplate(putIndexTemplateRequest, RequestOptions.DEFAULT);
        log.info("Indexing Template '{}' request ack={}", templateName, response.isAcknowledged());
    }

}
