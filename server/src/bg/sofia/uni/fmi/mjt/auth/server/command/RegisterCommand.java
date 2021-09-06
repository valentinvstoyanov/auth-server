package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.UnauthenticatedCommand;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.EMAIL;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.FIRST_NAME;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.LAST_NAME;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.PASSWORD;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class RegisterCommand extends UnauthenticatedCommand {

    public static final String NAME = "register";

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;

    public RegisterCommand(final UserService userService,
                           final CurrentSessionIdService currentSessionIdService,
                           final AuthenticationService authenticationService,
                           final AuthorizationService authorizationService) {
        super(currentSessionIdService);
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Set<String> requiredArgs() {
        return Set.of(USERNAME.toString(),
                PASSWORD.toString(),
                FIRST_NAME.toString(),
                LAST_NAME.toString(),
                EMAIL.toString());
    }

    @Override
    public Set<String> optionalArgs() {
        return null;
    }

    @Override
    protected String unauthenticatedExecute(final SocketChannel clientSocketChannel, final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());
        final String password = args.get(PASSWORD.toString());
        final String firstName = args.get(FIRST_NAME.toString());
        final String lastName = args.get(LAST_NAME.toString());
        final String email = args.get(EMAIL.toString());

        try {
            final User user = userService.createUser(username, password, firstName, lastName, email);
            authorizationService.assign(user.username(), CommonRoles.AUTHENTICATED.role);
            final String sessionId = authenticationService.authenticate(user.username(), user.password());
            currentSessionIdService.set(sessionId);
            return sessionId;
        } catch (UsernameAlreadyTakenException | InvalidUserDataException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
