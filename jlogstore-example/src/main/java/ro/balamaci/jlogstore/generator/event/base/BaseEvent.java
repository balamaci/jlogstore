package ro.balamaci.jlogstore.generator.event.base;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import org.slf4j.MDC;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author sbalamaci
 */
public abstract class BaseEvent implements Runnable {

    private int minWaitMs = 50;
    private int maxWaitMs = 250;


    public abstract void doWork();

    @Override
    public void run() {
        waitBeforeStart();

        MDC.put("timestampMillis", String.valueOf(System.currentTimeMillis()));

        doWork();
    }

    private void waitBeforeStart() {
        int waitMs = ThreadLocalRandom.current().nextInt(minWaitMs, maxWaitMs);
        try {
            Thread.sleep(waitMs);
        } catch (InterruptedException ignored) {  }
    }

    protected String randomUsername() {
        Fairy fairy = Fairy.create();
        Person person = fairy.person();
        return person.getEmail();
    }

    public void setMinWaitMs(int minWaitMs) {
        this.minWaitMs = minWaitMs;
    }

    public void setMaxWaitMs(int maxWaitMs) {
        this.maxWaitMs = maxWaitMs;
    }
}
