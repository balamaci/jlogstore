package ro.balamaci.jlogstore;

import ro.balamaci.jlogstore.publisher.es.ElasticSearchPublisher;
import ro.balamaci.jlogstore.server.Server;
import ro.balamaci.jlogstore.storage.ChronicleQueueStorage;
import ro.balamaci.jlogstore.tailer.ChronicleTailer;

/**
 * @author sbalamaci
 */
public class Start {

    public static void main(String[] args) {
        ChronicleQueueStorage chronicleQueueStorage = new ChronicleQueueStorage("./storage");

        ElasticSearchPublisher elasticSearchPublisher = new ElasticSearchPublisher();
        ChronicleTailer chronicleTailer = new ChronicleTailer(chronicleQueueStorage, elasticSearchPublisher, 100);

        Server server = new Server(7878, chronicleQueueStorage);
        server.start();
    }

}
