package ro.balamaci.jlogstore.publisher;

import com.codahale.metrics.ConsoleReporter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import ro.balamaci.jlogstore.server.RSocketServer;
import ro.balamaci.jlogstore.storage.ChronicleQueueStorage;
import ro.balamaci.jlogstore.tailer.ChronicleTailer;
import ro.balamaci.jlogstore.util.FileUtil;

import java.util.concurrent.TimeUnit;

public class StartPerf {

    private static final String JSON_FIELD_NAME_TIMESTAMP_MILLIS = "@timestamp";
    
    public static void main(String[] args) throws Exception {
        Config config = ConfigFactory.load("jlogstore-server.conf");


        String directory = config.getString("storage.dir");
        FileUtil.deleteDir(directory);

        ChronicleQueueStorage chronicleQueueStorage = new ChronicleQueueStorage(directory);

        TimestampMeasuringPublisher publisher = new TimestampMeasuringPublisher(JSON_FIELD_NAME_TIMESTAMP_MILLIS);
        ChronicleTailer chronicleTailer = new ChronicleTailer(chronicleQueueStorage, publisher, 100);
        chronicleTailer.start();

        initDropwizardReporter();

        RSocketServer RSocketServer = new RSocketServer(config.getString("server.bindAddress"),
                config.getInt("server.port"), chronicleQueueStorage);
        RSocketServer.start();
    }

    private static void initDropwizardReporter() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(ChronicleQueueStorage.metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(5, TimeUnit.SECONDS);
    }

}
