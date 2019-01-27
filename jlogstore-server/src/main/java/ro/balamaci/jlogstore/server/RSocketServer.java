package ro.balamaci.jlogstore.server;

import io.rsocket.AbstractRSocket;
import io.rsocket.Frame;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import ro.balamaci.jlogstore.storage.Storage;

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
        public Mono<Void> fireAndForget(Payload payload) {
            String json = payload.getDataUtf8();
            String clientId = payload.getMetadataUtf8();

            storage.store(clientId, json);
            return Mono.empty();
        }
    }

}
