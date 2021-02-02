package bg.sofia.uni.fmi.mjt.auth.server.command.unauthenticated;

import bg.sofia.uni.fmi.mjt.auth.server.session.service.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
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

    public RegisterCommand(final UserService userService, final CurrentSessionService currentSessionService) {
        super(currentSessionService);
        this.userService = userService;
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
    protected String unauthenticatedExecute(final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());
        final String password = args.get(PASSWORD.toString());
        final String firstName = args.get(FIRST_NAME.toString());
        final String lastName = args.get(LAST_NAME.toString());
        final String email = args.get(EMAIL.toString());
        try {
            final String sessionId = userService.register(username, password, firstName, lastName, email);
            currentSessionService.set(sessionId);
            return sessionId;
        } catch (UsernameAlreadyTakenException | InvalidUserDataException | IOException e) {
            return e.getMessage();
        }
    }

}
