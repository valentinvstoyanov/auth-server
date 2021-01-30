package bg.sofia.uni.fmi.mjt.auth.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class AuthServer {

    public static final String HOST = "localhost";
    public static final int PORT = 7777;
    public static final int BUFFER_SIZE = 2048;

    private Selector selector;
    private ByteBuffer buffer;

    private boolean isRunning;

    private final RequestHandler requestHandler;


    public AuthServer(final RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        this.isRunning = false;
    }

    public void start() {
        if (isRunning) {
            return;
        }

        try(ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(HOST, PORT));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            allocateBuffer();

            isRunning = true;
            System.out.println("Server started at port: " + PORT);
            selectionLoop();
        } catch (final IOException ioException) {
            System.out.println("Failed to start the server: " + ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    public void stop() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
        buffer.clear();
        System.out.println("Server stopped.");
    }

    private void allocateBuffer() {
        if (buffer == null) {
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
        }
    }

    private void selectionLoop() throws IOException {
        while (isRunning) {
            selector.select();
            final Set<SelectionKey> selectedKeys = selector.selectedKeys();
            final Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                final SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    acceptClient((ServerSocketChannel) key.channel());
                } else if (key.isReadable()) {
                    processRequest((SocketChannel) key.channel());
                }

                keyIterator.remove();
            }
        }
    }

    private void acceptClient(final ServerSocketChannel serverSocketChannel) throws IOException {
        final SocketChannel clientSocketChannel = serverSocketChannel.accept();
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void processRequest(final SocketChannel clientSocketChannel) {
        String response;
        try {
            final String request = readClientRequest(clientSocketChannel);
            if (request == null) {
                return;
            }
            requestHandler.handle(request);
            response = "TODO";
        } catch (IOException readIoException) {
            response = "Failed to read and process your request. Please try again later.";
            System.out.println("Failed to read client request: " + readIoException.getMessage());
            readIoException.printStackTrace();
        }
        writeClientResponse(clientSocketChannel, response);
    }

    private String readClientRequest(final SocketChannel clientSocketChannel) throws IOException {
        buffer.clear();
        if (clientSocketChannel.read(buffer) < 0) {
            try {
                clientSocketChannel.close();
                System.out.println("Client closed the connection.");
            } catch (final IOException closeIoException) {
                System.out.println("Failed to close client socket channel: " + closeIoException.getMessage());
                closeIoException.printStackTrace();
            }
            return null;
        }

        buffer.flip();
        return new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8);
    }

    private void writeClientResponse(final SocketChannel clientSocketChannel, final String response) {
        buffer.clear();
        buffer.put(response.getBytes());

        buffer.flip();
        try {
            clientSocketChannel.write(buffer);
        } catch (IOException writeIoException) {
            System.out.println("Failed to write response to client: " + writeIoException.getMessage());
            writeIoException.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AuthServer authServer = new AuthServer(null);
        authServer.start();
    }

}
