package bg.sofia.uni.fmi.mjt.auth.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AuthClient {
    private static final int BUFFER_SIZE = 2048;
    private static final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(AuthServer.HOST, AuthServer.PORT));

            while (true) {
                System.out.print("=> ");
                final String message = scanner.nextLine();

                buffer.put(message.getBytes());
                buffer.flip();
                socketChannel.write(buffer);

                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();

                final String reply = new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8);
                buffer.clear();
                System.out.println(reply);
            }
        } catch (IOException e) {
            System.out.println("There is a problem with the network communication.");
            e.printStackTrace();
        }
    }

}
