package bg.sofia.uni.fmi.mjt.auth.server.request;

import java.nio.channels.SocketChannel;

public interface RequestHandler {

    String handle(SocketChannel clientSocketChannel, String request);

}
