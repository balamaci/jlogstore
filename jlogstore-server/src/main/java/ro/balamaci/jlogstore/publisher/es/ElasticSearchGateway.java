package ro.balamaci.jlogstore.publisher.es;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.List;

public class ElasticSearchGateway {

    private RestHighLevelClient restClient;

    public ElasticSearchGateway(List<String> hostsList) {
        HttpHost[] hosts = hostsList
                .stream()
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);

        restClient = new RestHighLevelClient(RestClient.builder(hosts));
    }

    void index(String json) throws IOException  {
        IndexRequest indexRequest = new IndexRequest("people");
        indexRequest.source(json, XContentType.JSON);

        restClient.index(indexRequest, RequestOptions.DEFAULT);
    }

}
