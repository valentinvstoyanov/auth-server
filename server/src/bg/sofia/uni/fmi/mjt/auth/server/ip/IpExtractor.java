package bg.sofia.uni.fmi.mjt.auth.server.ip;

import java.nio.channels.SocketChannel;

public interface IpExtractor {

    String extract(final SocketChannel socketChannel);

}
