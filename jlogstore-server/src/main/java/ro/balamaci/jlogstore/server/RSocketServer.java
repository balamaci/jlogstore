package ro.balamaci.jlogstore.server;

import io.rsocket.AbstractRSocket;
import io.rsocket.Frame;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import ro.balamaci.jlogstore.storage.Storage;

import java.util.concurrent.CountDownLatch;

/**
 * @author sbalamaci
 */
public class RSocketServer {

    private final String bindAddress;
    private final int port;

    private Storage storage;

    private static final Logger log = LoggerFactory.getLogger(RSocketServer.class);

    public RSocketServer(String bindAddress, int port, Storage storage) {
        this.port = port;
        this.bindAddress = bindAddress;
        this.storage = storage;
    }

    public void start() {
        RSocketFactory.receive()
                .frameDecoder(Frame::retain)
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(new LogServerReceiver()))
                .transport(TcpServerTransport.create(bindAddress, port))
                .start()
                .block()
                .onClose()
                .block();
    }

    private class LogServerReceiver extends AbstractRSocket {

        @Override
        public Mono<Payload> requestResponse(Payload payload) {
            System.out.println("Got " + payload.getDataUtf8());
            return Mono.just(DefaultPayload.create("Accept"));
        }

        @Override
        public Mono<Void> fireAndForget(Payload payload) {
            String json = payload.getDataUtf8();

            log.info("Received {}", json);
            storage.store("test", json);
            return Mono.empty();
        }
    }

}
