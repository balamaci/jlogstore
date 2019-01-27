package ro.balamaci.jlogstore.logback.appender;

import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import net.logstash.logback.appender.AsyncDisruptorAppender;
import net.logstash.logback.appender.destination.DestinationParser;
import net.logstash.logback.appender.listener.TcpAppenderListener;
import net.logstash.logback.encoder.com.lmax.disruptor.EventHandler;
import net.logstash.logback.encoder.com.lmax.disruptor.LifecycleAware;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class borrows much from the {@link net.logstash.logback.appender.AbstractLogstashTcpSocketAppender }
 *
 * @author sbalamaci
 */
public class LogbackRSocketAppender<Event extends DeferredProcessingAware, Listener extends TcpAppenderListener<Event>>
        extends AsyncDisruptorAppender<Event, Listener> {

    private volatile boolean started = false;

    /**
     * The encoder which is ultimately responsible for writing the event
     * which we assume to be the JSon encoder
     */
    private Encoder<Event> encoder;

    private String clientId = "jlogstore";

    /**
     * Destinations to which to attempt to send logs, in order of preference.
     * <p>
     *
     * Logs are only sent to one destination at a time.
     * <p>
     */
    private List<InetSocketAddress> destinations = new ArrayList<InetSocketAddress>(2);

    /**
     * The default port number of remote logging server (7878).
     */
    public static final int DEFAULT_PORT = 7878;

    /**
     * Event handler responsible for performing the RSocket transmission.
     */
    private class RSocketSendingEventHandler implements EventHandler<LogEvent<Event>>, LifecycleAware {
        private RSocket socket;

        private synchronized RSocket openRSocketConnection() {
            InetSocketAddress destination = destinations.get(0);
            return RSocketFactory.connect()
                            .transport(TcpClientTransport.create(getHostString(destination), destination.getPort()))
                            .start()
                            .block();
        }

        @Override
        public void onEvent(LogEvent<Event> eventLogEvent, long sequence, boolean endOfBatch) throws Exception {
            byte[] json = encoder.encode(eventLogEvent.event);
            socket.fireAndForget(DefaultPayload.create(json, clientId.getBytes())).subscribe();
        }

        @Override
        public void onStart() {
            try {
                socket = openRSocketConnection();
            } catch (Exception e) {
                addError("Error opening RSocket", e);
            }
        }

        @Override
        public void onShutdown() {
            encoder.stop();
        }
    }

    public LogbackRSocketAppender() {
        setEventHandler(new RSocketSendingEventHandler());
    }

    @Override
    public synchronized void start() {
        if (isStarted()) {
            return;
        }

        int errorCount = 0;
        if (encoder == null) {
            errorCount++;
            addError("No encoder was configured. Use <encoder> to specify the fully qualified class name of the encoder to use");
        }

        /*
         * Make sure at least one destination has been specified
         */
        if (destinations.isEmpty()) {
            errorCount++;
            addError("No destination was configured. Use <destination> to add one or more destinations to the appender");
        }

        if (errorCount == 0) {
            encoder.setContext(getContext());
            if (!encoder.isStarted()) {
                encoder.start();
            }

            started = true;
            super.start();
        }
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    public Encoder<Event> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<Event> encoder) {
        this.encoder = encoder;
    }

    /**
     * Adds the given destination (or destinations) to the list of potential destinations
     * to which to send logs.
     * <p>
     *
     * The string is a comma separated list of destinations in the form of hostName[:portNumber].
     * <p>
     * If portNumber is not provided, defaults to {@value #DEFAULT_PORT}
     * <p>
     *
     * For example, "host1.domain.com,host2.domain.com:5560"
     */
    public void addDestination(final String destination) throws IllegalArgumentException {

        List<InetSocketAddress> parsedDestinations = DestinationParser.parse(destination, DEFAULT_PORT);

        addDestinations(parsedDestinations.toArray(new InetSocketAddress[parsedDestinations.size()]));
    }

    /**
     * Adds the given destinations to the list of potential destinations.
     */
    public void addDestinations(InetSocketAddress... destinations) throws IllegalArgumentException  {
        if (destinations == null) {
            return;
        }

        for (InetSocketAddress destination : destinations) {
            try {
                InetAddress.getByName(getHostString(destination));
            }
            catch (UnknownHostException ex) {
                /*
                 * Warn, but don't fail startup, so that transient
                 * DNS problems are allowed to resolve themselves eventually.
                 */
                addWarn("Invalid destination '" + getHostString(destination) + "': host unknown (was '" + getHostString(destination) + "').");
            }
            this.destinations.add(destination);
        }
    }

    /**
     * Returns the host string from the given destination,
     * avoiding a DNS hit if possible.
     */
    protected String getHostString(InetSocketAddress destination) {
        /*
         * Avoid the potential DNS hit by using getHostString() instead of getHostName()
         */
        return destination.getHostString();
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
