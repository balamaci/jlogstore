package ro.balamaci.jlogstore.publisher.es;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
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

    void index(String index, String json) throws IOException  {
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.source(json, XContentType.JSON);

        IndexResponse indexResponse = restClient.index(indexRequest, RequestOptions.DEFAULT);
        int status = indexResponse.status().getStatus();
        System.out.println("status " + status);
    }

    void putIndexTemplate(String templateName, String json) throws IOException  {
        PutIndexTemplateRequest putIndexTemplateRequest = new PutIndexTemplateRequest(templateName);
        putIndexTemplateRequest.source(json, XContentType.JSON);

        restClient.indices().putTemplate(putIndexTemplateRequest, RequestOptions.DEFAULT);
    }

}
