package ro.balamaci.jlogstore;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import ro.balamaci.jlogstore.publisher.es.ElasticSearchPublisher;
import ro.balamaci.jlogstore.server.RSocketServer;
import ro.balamaci.jlogstore.storage.ChronicleQueueStorage;
import ro.balamaci.jlogstore.tailer.ChronicleTailer;

import java.util.List;

/**
 * @author sbalamaci
 */
public class Start {

    public static void main(String[] args) {
        Config config = ConfigFactory.load("jlogstore-server.conf");
        List<String> hosts = config.getStringList("elastic.endpoints");

        String directory = config.getString("storage.dir");
        ChronicleQueueStorage chronicleQueueStorage = new ChronicleQueueStorage(directory);

        ElasticSearchPublisher elasticSearchPublisher = new ElasticSearchPublisher(hosts);
        ChronicleTailer chronicleTailer = new ChronicleTailer(chronicleQueueStorage, elasticSearchPublisher, 100);

        RSocketServer RSocketServer = new RSocketServer(config.getInt("server.port"), chronicleQueueStorage);
        RSocketServer.start();
    }

}
