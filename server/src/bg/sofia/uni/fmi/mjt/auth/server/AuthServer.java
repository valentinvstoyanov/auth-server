package bg.sofia.uni.fmi.mjt.auth.server;

import bg.sofia.uni.fmi.mjt.auth.server.audit.AuditLog;
import bg.sofia.uni.fmi.mjt.auth.server.audit.AuditLogImpl;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.model.UsernameSession;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.repository.AuthenticationRepository;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.repository.KeyValueSessionAuthenticationRepository;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.repository.SessionSerializer;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationServiceImpl;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdServiceImpl;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.repository.AuthorizationRepository;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.repository.KeyValueAuthorizationRepository;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.repository.RoleSerializer;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationServiceImpl;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.RoleMatcher;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.RoleMatcherImpl;
import bg.sofia.uni.fmi.mjt.auth.server.command.AddAdminCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.DeleteUserCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.LoginCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.LogoutCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.RegisterCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.RemoveAdminCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.UpdatePasswordCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.UpdateUserCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.Command;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.CommandParser;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.NameArgsCommandParser;
import bg.sofia.uni.fmi.mjt.auth.server.command.validator.CommandValidator;
import bg.sofia.uni.fmi.mjt.auth.server.command.validator.ParsedCommandValidator;
import bg.sofia.uni.fmi.mjt.auth.server.ip.IpExtractor;
import bg.sofia.uni.fmi.mjt.auth.server.ip.IpExtractorImpl;
import bg.sofia.uni.fmi.mjt.auth.server.request.CommandRequestHandler;
import bg.sofia.uni.fmi.mjt.auth.server.request.RequestHandler;
import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.FileKeyValueStorage;
import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.KeyValueDataStore;
import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.MemoryKeyValueStorage;
import bg.sofia.uni.fmi.mjt.auth.server.storage.serializer.Serializer;
import bg.sofia.uni.fmi.mjt.auth.server.storage.serializer.StringSerializer;
import bg.sofia.uni.fmi.mjt.auth.server.user.encoder.IdentityPasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.server.user.encoder.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.KeyValueUserRepository;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.UserRepository;
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

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
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
            response = requestHandler.handle(clientSocketChannel, request);
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
        String userFilename = "test-users";
        String authnFilename = "test-authn";
        String authzFilename = "test-authz";
        String logFilename = "test-logs";

        Duration sessionDuration = Duration.ofDays(1);

        PasswordEncoder passwordEncoder = new IdentityPasswordEncoder();

        Serializer<String> stringSerializer = new StringSerializer();
        Serializer<User> userSerializer = new UserSerializer();

        KeyValueDataStore<String, User> userStore = new FileKeyValueStorage<>(userFilename,
                stringSerializer,
                userSerializer);
        KeyValueDataStore<String, User> userCache = new MemoryKeyValueStorage<>();

        UserValidator userValidator = new UserValidatorImpl();
        UserRepository userRepository = new KeyValueUserRepository(userStore, userCache);
        UserService userService = new UserServiceImpl(userRepository, userValidator, passwordEncoder);

        Map<String, Command> commands = new HashMap<>();

        CommandValidator commandValidator = new ParsedCommandValidator();
        CommandParser commandParser = new NameArgsCommandParser();
        RequestHandler requestHandler = new CommandRequestHandler(commandValidator, commandParser, commands);
        AuthServer authServer = new AuthServer(requestHandler);

        Serializer<Role> roleSerializer = new RoleSerializer(stringSerializer);
        KeyValueDataStore<String, Role> authzStore = new FileKeyValueStorage<>(authzFilename, stringSerializer, roleSerializer);
        KeyValueDataStore<String, Role> authzCache = new MemoryKeyValueStorage<>();
        AuthorizationRepository authorizationRepository = new KeyValueAuthorizationRepository(authzStore, authzCache);
        RoleMatcher roleMatcher = new RoleMatcherImpl();
        AuthorizationService authorizationService = new AuthorizationServiceImpl(authorizationRepository, roleMatcher);

        Serializer<Session> sessionSerializer = new SessionSerializer();
        KeyValueDataStore<String, Session> authnStore = new FileKeyValueStorage<>(authnFilename, stringSerializer, sessionSerializer);
        KeyValueDataStore<String, UsernameSession> authnCache = new MemoryKeyValueStorage<>();
        AuthenticationRepository authenticationRepository = new KeyValueSessionAuthenticationRepository(authnStore, authnCache);
        AuthenticationService authenticationService = new AuthenticationServiceImpl(sessionDuration, authenticationRepository, userService, passwordEncoder);

        CurrentSessionIdService currentSessionIdService = new CurrentSessionIdServiceImpl(authServer::getCurrentSelectionKey);

        KeyValueDataStore<String, String> logStore = new FileKeyValueStorage<>(logFilename, stringSerializer, stringSerializer);
        AuditLog auditLog = new AuditLogImpl(logStore);

        IpExtractor ipExtractor = new IpExtractorImpl();

        Command loginCommand = new LoginCommand(currentSessionIdService, authenticationService, auditLog, ipExtractor);
        Command registerCommand = new RegisterCommand(userService, currentSessionIdService, authenticationService, authorizationService);
        Command updateCommand = new UpdateUserCommand(currentSessionIdService, authenticationService, authorizationService, userService);
        Command resetCommand = new UpdatePasswordCommand(currentSessionIdService, authenticationService, authorizationService, userService);
        Command logoutCommand = new LogoutCommand(currentSessionIdService, authenticationService);
        Command addAdminCommand = new AddAdminCommand(currentSessionIdService, authenticationService, authorizationService, auditLog, ipExtractor);
        Command removeAdminCommand = new RemoveAdminCommand(currentSessionIdService, authenticationService, authorizationService, auditLog, ipExtractor);
        Command deleteUserCommand = new DeleteUserCommand(currentSessionIdService, authenticationService, authorizationService, userService);

        commands.put(loginCommand.name(), loginCommand);
        commands.put(registerCommand.name(), registerCommand);
        commands.put(updateCommand.name(), updateCommand);
        commands.put(resetCommand.name(), resetCommand);
        commands.put(logoutCommand.name(), logoutCommand);
        commands.put(addAdminCommand.name(), addAdminCommand);
        commands.put(removeAdminCommand.name(), removeAdminCommand);
        commands.put(deleteUserCommand.name(), deleteUserCommand);

        if (userCache.getAll().isEmpty()) {
            final User admin = new User("admin", "admin", "admin", "admin", "admin");
            userRepository.createUser(admin);
            authenticationService.authenticate(admin.username(), admin.password());
            authorizationService.assign(admin.username(), CommonRoles.ADMIN.role);
            System.out.println("admin added.");
        }

        authServer.start();
    }

}
