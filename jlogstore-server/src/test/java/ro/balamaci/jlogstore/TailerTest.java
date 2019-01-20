package ro.balamaci.jlogstore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.support.io.TempDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.balamaci.jlogstore.generator.FakeJsonLogEventGenerator;
import ro.balamaci.jlogstore.publisher.InMemoryPublisher;
import ro.balamaci.jlogstore.storage.ChronicleQueueStorage;
import ro.balamaci.jlogstore.tailer.ChronicleTailer;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sbalamaci
 */
public class TailerTest {

    private static final Logger log = LoggerFactory.getLogger(TailerTest.class);

    @Test
    @ExtendWith(TempDirectory.class)
    public void testTailerRetrievesAndPushesDataToPublisher(@TempDirectory.TempDir Path tempDir) throws Exception {
        ChronicleQueueStorage chronicleQueueStorage = new ChronicleQueueStorage(tempDir.toAbsolutePath().toString());

        FakeJsonLogEventGenerator generator = new FakeJsonLogEventGenerator();
        List<String> logEvents = generator.generateJsonLogEvents(1000);

        String logId = "test-logger";

        logEvents.forEach(event -> {
            chronicleQueueStorage.store(logId, event);
        });

        InMemoryPublisher memoryPublisher = new InMemoryPublisher();
        ChronicleTailer chronicleTailer = new ChronicleTailer(chronicleQueueStorage, memoryPublisher, 5);
        chronicleTailer.start();

        chronicleTailer.signalShutdownIfNoNewDataAvailable();
        chronicleTailer.awaitShutdown(1, TimeUnit.MINUTES);

        List<String> publishedEvents = memoryPublisher.getPublished(logId);
        log.info("Published {}", publishedEvents.size());
        publishedEvents.stream().limit(10).forEach((json) -> log.info("Event {}", json));
    }

}
