package ro.balamaci.jlogstore;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import ro.balamaci.jlogstore.publisher.es.ElasticSearchPublisher;
import ro.balamaci.jlogstore.server.RSocketServer;
import ro.balamaci.jlogstore.storage.ChronicleQueueStorage;
import ro.balamaci.jlogstore.tailer.ChronicleTailer;

import java.io.IOException;
import java.util.List;

/**
 * @author sbalamaci
 */
public class StartServer {

    public static void main(String[] args) throws IOException {
        Config config = ConfigFactory.load("jlogstore-server.conf");
        List<String> hosts = config.getStringList("elastic.endpoints");

        String directory = config.getString("storage.dir");
        ChronicleQueueStorage chronicleQueueStorage = new ChronicleQueueStorage(directory);

        String indexTemplateDir = config.getString("elastic.templates_dir");
        ElasticSearchPublisher publisher = new ElasticSearchPublisher(hosts);
        publisher.registerIndexTemplate(indexTemplateDir);

//        SimpleLogPublisher publisher = new SimpleLogPublisher();
        ChronicleTailer chronicleTailer = new ChronicleTailer(chronicleQueueStorage, publisher, 100);
        chronicleTailer.start();

        RSocketServer RSocketServer = new RSocketServer(config.getString("server.bindAddress"),
                config.getInt("server.port"), chronicleQueueStorage);
        RSocketServer.start();
    }

}
