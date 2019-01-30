package ro.balamaci.jlogstore.storage;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sbalamaci
 */
public class ChronicleQueueStorage implements Storage {

    private ConcurrentHashMap<String, ChronicleQueue> queues = new ConcurrentHashMap<>();

    private String chronicleStorageDir;

    public static final MetricRegistry metrics = new MetricRegistry();
    private final Meter storageCounter = metrics.meter("storageCounter");

    private static final Logger log = LoggerFactory.getLogger(ChronicleQueueStorage.class);

    public ChronicleQueueStorage(String chronicleStorageDir) {
        this.chronicleStorageDir = chronicleStorageDir;
    }

    @Override
    public void store(String clientLoggerId, String json) {
        ChronicleQueue chronicleQueue = queues.computeIfAbsent(clientLoggerId, this::initQueue);
        try {
            ExcerptAppender appender = chronicleQueue.acquireAppender();
            appender.writeText(json);
            storageCounter.mark();
        } catch (Exception e) {
            log.error("Error persisting message", e);
        }
    }

    private ChronicleQueue initQueue(String clientLoggerId) {
        log.info("Initializing Queue for {}", clientLoggerId);
        return ChronicleQueue.singleBuilder(Paths.get(chronicleStorageDir, clientLoggerId)).build();
    }

    @Override
    public void close() {
        queues.values().forEach(Closeable::close);
    }

    public ConcurrentHashMap<String, ChronicleQueue> getQueues() {
        return queues;
    }
}
