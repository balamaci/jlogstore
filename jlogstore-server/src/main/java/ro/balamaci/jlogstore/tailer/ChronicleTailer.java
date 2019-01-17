package ro.balamaci.jlogstore.tailer;

import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import ro.balamaci.jlogstore.es.ElasticSearchPublisher;
import ro.balamaci.jlogstore.storage.ChronicleQueueStorage;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author sbalamaci
 */
public class ChronicleTailer {

    private volatile boolean shouldStop = false;

    private Map<String, ExcerptTailer> tailerMap = new HashMap<>();

    private static final int SLEEP_MILLIS_NO_DATA = 1000;

    public ChronicleTailer(ChronicleQueueStorage storage,
                           ElasticSearchPublisher elasticSearchPublisher, int itemsToRead) {


        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                boolean dataWasAvailable = false;

                Enumeration<String> clientIds = storage.getQueues().keys();
                while (clientIds.hasMoreElements()) {
                    String clientId = clientIds.nextElement();
                    ExcerptTailer tailer = tailerMap.computeIfAbsent(clientId,
                            (id) -> createTailer(storage.getQueues().get(id)));

                    int readItems = 0;
                    while (tailer.peekDocument()) {
                        String json = tailer.readText();

                        readItems++;
                        dataWasAvailable = true;

                        if (readItems > itemsToRead) {
                            break;
                        }
                    }
                }

                if (shouldStop) {
                    break;
                } else {
                    if (!dataWasAvailable) {
                        try {
                            Thread.sleep(SLEEP_MILLIS_NO_DATA);
                        } catch (InterruptedException ignore) {
                        }
                    }
                }
            }
        });
    }

    private ExcerptTailer createTailer(ChronicleQueue chronicleQueue) {
        return chronicleQueue.createTailer();
    }

    public void shutdown() {
        shouldStop = true;
    }

}
