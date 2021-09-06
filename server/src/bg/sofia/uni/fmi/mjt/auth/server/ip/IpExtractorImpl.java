package bg.sofia.uni.fmi.mjt.auth.server.ip;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class IpExtractorImpl implements IpExtractor {

    @Override
    public String extract(final SocketChannel socketChannel) {
        try {
            return socketChannel.getRemoteAddress().toString();
        } catch (final IOException ioException) {
            System.out.println("Failed to get IP address");
            ioException.printStackTrace();
            return "";
        }
    }

}
