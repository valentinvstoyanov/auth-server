package bg.sofia.uni.fmi.mjt.auth.server;

import bg.sofia.uni.fmi.mjt.auth.server.command.Command;
import bg.sofia.uni.fmi.mjt.auth.server.command.authenticated.LogoutCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.authenticated.UpdatePasswordCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.authenticated.UpdateUserCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.unauthenticated.LoginCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.unauthenticated.RegisterCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.CommandParser;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.NameArgsCommandParser;
import bg.sofia.uni.fmi.mjt.auth.server.command.validator.CommandValidator;
import bg.sofia.uni.fmi.mjt.auth.server.command.validator.ParsedCommandValidator;
import bg.sofia.uni.fmi.mjt.auth.server.request.CommandRequestHandler;
import bg.sofia.uni.fmi.mjt.auth.server.request.RequestHandler;
import bg.sofia.uni.fmi.mjt.auth.server.session.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.session.model.UsernameSession;
import bg.sofia.uni.fmi.mjt.auth.server.session.repository.SessionRepository;
import bg.sofia.uni.fmi.mjt.auth.server.session.repository.SessionRepositoryImpl;
import bg.sofia.uni.fmi.mjt.auth.server.session.repository.SessionSerializer;
import bg.sofia.uni.fmi.mjt.auth.server.session.repository.UsernameSessionSerializer;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.CurrentSessionServiceImpl;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionServiceImpl;
import bg.sofia.uni.fmi.mjt.auth.server.storage.FileKeyValueStorage;
import bg.sofia.uni.fmi.mjt.auth.server.storage.KeyValueDataStore;
import bg.sofia.uni.fmi.mjt.auth.server.storage.MemoryKeyValueStorage;
import bg.sofia.uni.fmi.mjt.auth.server.storage.Serializer;
import bg.sofia.uni.fmi.mjt.auth.server.storage.StringSerializer;
import bg.sofia.uni.fmi.mjt.auth.server.user.encoder.IdentityPasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.server.user.encoder.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.UserRepositoryImpl;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.UserSerializer;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserServiceImpl;
import bg.sofia.uni.fmi.mjt.auth.server.user.validator.UserValidator;
import bg.sofia.uni.fmi.mjt.auth.server.user.validator.UserValidatorImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AuthServer {

    public static final String HOST = "localhost";
    public static final int PORT = 7777;
    public static final int BUFFER_SIZE = 2048;

    private Selector selector;
    private ByteBuffer buffer;

    private boolean isRunning;

    private final RequestHandler requestHandler;
    private SelectionKey currentSelectionKey;

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
                currentSelectionKey = key;

                if (key.isAcceptable()) {
                    acceptClient((ServerSocketChannel) key.channel());
                } else if (key.isReadable()) {
                    processRequest((SocketChannel) key.channel());
                }

                keyIterator.remove();
                currentSelectionKey = null;
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
            response = requestHandler.handle(request);
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

    public SelectionKey getCurrentSelectionKey() {
        return currentSelectionKey;
    }

    public static void main(String[] args) throws IOException {
        String userFileName = "test-users";
        String sessionFileName = "test-sessions";
        Duration sessionDuration = Duration.ofDays(1);

        PasswordEncoder passwordEncoder = new IdentityPasswordEncoder();

        Serializer<String> stringSerializer = new StringSerializer();
        Serializer<Session> sessionSerializer = new SessionSerializer();
        Serializer<UsernameSession> usernameSessionSerializer = new UsernameSessionSerializer(sessionSerializer);
        Serializer<User> userSerializer = new UserSerializer();

        KeyValueDataStore<String, Session> sessionStore = new FileKeyValueStorage<>(sessionFileName,
                stringSerializer,
                sessionSerializer);
        KeyValueDataStore<String, UsernameSession> sessionCache = new MemoryKeyValueStorage<>();

        SessionRepository sessionRepository = new SessionRepositoryImpl(sessionStore, sessionCache);
        SessionService sessionService = new SessionServiceImpl(sessionDuration, sessionRepository);

        KeyValueDataStore<String, User> userStore = new FileKeyValueStorage<>(userFileName,
                stringSerializer,
                userSerializer);
        KeyValueDataStore<String, User> userCache = new MemoryKeyValueStorage<>();

        UserValidator userValidator = new UserValidatorImpl();
        UserRepository userRepository = new UserRepositoryImpl(userStore, userCache);
        UserService userService = new UserServiceImpl(userRepository, userValidator, sessionService, passwordEncoder);

        Map<String, Command> commands = new HashMap<>();

        CommandValidator commandValidator = new ParsedCommandValidator();
        CommandParser commandParser = new NameArgsCommandParser();
        RequestHandler requestHandler = new CommandRequestHandler(commandValidator, commandParser, commands);
        AuthServer authServer = new AuthServer(requestHandler);

        CurrentSessionService currentSessionService = new CurrentSessionServiceImpl(authServer::getCurrentSelectionKey);
        Command loginCommand = new LoginCommand(userService, currentSessionService);
        Command registerCommand = new RegisterCommand(userService, currentSessionService);
        Command updateCommand = new UpdateUserCommand(currentSessionService, sessionService, userService);
        Command resetCommand = new UpdatePasswordCommand(currentSessionService, sessionService, userService);
        Command logoutCommand = new LogoutCommand(currentSessionService, sessionService, userService);

        commands.put(loginCommand.name(), loginCommand);
        commands.put(registerCommand.name(), registerCommand);
        commands.put(updateCommand.name(), updateCommand);
        commands.put(resetCommand.name(), resetCommand);
        commands.put(logoutCommand.name(), logoutCommand);

        authServer.start();
    }

}
