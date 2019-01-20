package ro.balamaci.jlogstore.storage;

import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sbalamaci
 */
public class ChronicleQueueStorage implements Storage {

    private ConcurrentHashMap<String, ChronicleQueue> queues = new ConcurrentHashMap<>();

    private String chronicleStorageDir;

    public ChronicleQueueStorage(String chronicleStorageDir) {
        this.chronicleStorageDir = chronicleStorageDir;
    }

    @Override
    public void store(String loggerId, String json) {
        ChronicleQueue chronicleQueue = queues.computeIfAbsent(loggerId, this::initQueue);
        ExcerptAppender appender = chronicleQueue.acquireAppender();
        appender.writeText(json);
    }

    private ChronicleQueue initQueue(String id) {
        return ChronicleQueue.singleBuilder(Paths.get(chronicleStorageDir, id)).build();
    }

    @Override
    public void close() {
        queues.values().forEach(Closeable::close);
    }

    public ConcurrentHashMap<String, ChronicleQueue> getQueues() {
        return queues;
    }
}
