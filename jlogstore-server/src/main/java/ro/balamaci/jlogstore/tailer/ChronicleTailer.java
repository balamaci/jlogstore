package ro.balamaci.jlogstore.tailer;

import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import ro.balamaci.jlogstore.publisher.Publisher;
import ro.balamaci.jlogstore.storage.ChronicleQueueStorage;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author sbalamaci
 */
public class ChronicleTailer {

    private volatile boolean shouldStop = false;
    private volatile boolean shouldStopIfNoDataAvailable = false;

    private Map<String, ExcerptTailer> tailerMap = new HashMap<>();

    private static final int SLEEP_MILLIS_NO_DATA = 1000;

    private ChronicleQueueStorage storage;
    private Publisher publisher;
    private int itemsToRead;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ChronicleTailer(ChronicleQueueStorage storage, Publisher publisher, int itemsToRead) {
        this.storage = storage;
        this.publisher = publisher;
        this.itemsToRead = itemsToRead;
    }

    private ExcerptTailer createExcerptTailer(ChronicleQueue chronicleQueue) {
        return chronicleQueue.createTailer();
    }

    public void start() {
        executorService.submit(() -> {
            while (true) {
                boolean dataWasAvailable = false;

                Enumeration<String> logIds = storage.getQueues().keys();
                while (logIds.hasMoreElements()) {
                    String logId = logIds.nextElement();
                    ExcerptTailer tailer = tailerMap.computeIfAbsent(logId,
                            (id) -> createExcerptTailer(storage.getQueues().get(id)));

                    int readItems = 0;
                    while (tailer.peekDocument()) {
                        String json = tailer.readText();

                        readItems++;
                        dataWasAvailable = true;

                        publisher.publish(logId, json);

                        if (readItems > itemsToRead) {
                            break;
                        }
                    }
                }

                if (shouldStop) {
                    break;
                } else {
                    if (!dataWasAvailable) {
                        if(shouldStopIfNoDataAvailable) {
                            break;
                        }

                        try {
                            Thread.sleep(SLEEP_MILLIS_NO_DATA);
                        } catch (InterruptedException ignore) { }
                    }
                }
            }
        });
    }

    public void signalShutdown() {
        shouldStop = true;
    }

    public void signalShutdownIfNoNewDataAvailable() {
        shouldStopIfNoDataAvailable = true;
    }

    public void awaitShutdown(long timeout, TimeUnit unit) throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(timeout, unit);
    }

}
