package ro.balamaci.jlogstore.server;

import io.rsocket.AbstractRSocket;
import io.rsocket.Frame;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.publisher.Mono;
import ro.balamaci.jlogstore.storage.Storage;

/**
 * @author sbalamaci
 */
public class Server {

    private final int port;

    private Storage storage;

    public Server(int port, Storage storage) {
        this.port = port;
        this.storage = storage;
    }

    public void start() {
        RSocketFactory.receive()
                .frameDecoder(Frame::retain)
                .acceptor((setupPayload, reactiveSocket) -> Mono.just(new LogServerReceiver()))
                .transport(TcpServerTransport.create(port))
                .start()
                .block()
                .onClose();
    }

    private class LogServerReceiver extends AbstractRSocket {

        @Override
        public Mono<Void> fireAndForget(Payload payload) {
            storage.store(payload.getMetadataUtf8(), payload.getDataUtf8());
            return Mono.empty();
        }
    }

}
