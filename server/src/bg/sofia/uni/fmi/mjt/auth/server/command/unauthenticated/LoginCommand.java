package bg.sofia.uni.fmi.mjt.auth.server.command.unauthenticated;

import bg.sofia.uni.fmi.mjt.auth.server.session.service.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.PASSWORD;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class LoginCommand extends UnauthenticatedCommand {

    public static final String NAME = "login";

    private final UserService userService;

    public LoginCommand(final UserService userService, final CurrentSessionService currentSessionService) {
        super(currentSessionService);
        this.userService = userService;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Set<String> requiredArgs() {
        return Set.of(USERNAME.toString(), PASSWORD.toString());
    }

    @Override
    public Set<String> optionalArgs() {
        return null;
    }

    @Override
    protected String unauthenticatedExecute(final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());
        final String password = args.get(PASSWORD.toString());
        try {
            final String sessionId = userService.login(username, password);
            currentSessionService.set(sessionId);
            return sessionId;
        } catch (InvalidUserDataException | InvalidUsernamePasswordCombination | IOException e) {
            return e.getMessage();
        }
    }

}
