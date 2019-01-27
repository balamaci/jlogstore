package ro.balamaci.jlogstore.publisher.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.balamaci.jlogstore.publisher.PublishException;
import ro.balamaci.jlogstore.publisher.Publisher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

/**
 * @author sbalamaci
 */
public class ElasticSearchPublisher implements Publisher {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchPublisher.class);

    private ElasticSearchGateway elasticSearchGateway;

    public ElasticSearchPublisher(List<String> hosts) {
        this.elasticSearchGateway = new ElasticSearchGateway(hosts);
    }

    public void registerIndexTemplate(String indexTemplateDir) throws IOException {
        Path indexTemplateDirectory = Paths.get(indexTemplateDir);

        Files.walk(indexTemplateDirectory)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(file -> !file.isDirectory())
                .forEach(file -> {
                    try {
                        String templateContent = new String(Files.readAllBytes(file.toPath()));
                        log.info("Applying indexing template file '{}'", file.getName());
                        elasticSearchGateway.putIndexTemplate(file.getName(), templateContent);
                        log.info("Applied indexing template file '{}'", file.getName());
                    } catch (IOException e) {
                        throw new RuntimeException("Could not load template file " + file.getAbsolutePath(), e);
                    }
                });
    }

    @Override
    public void publish(String logId, String json) {
        log.info("Indexing to ES {}", json);
        try {
            elasticSearchGateway.index(logId, json);
        } catch (IOException e) {
            throw new PublishException(e);
        }
    }
}
