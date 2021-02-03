package bg.sofia.uni.fmi.mjt.auth.server.command.types.unauthenticated;

import bg.sofia.uni.fmi.mjt.auth.server.session.service.maybedelete.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.PASSWORD;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.SESSION_ID;
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
        return null;
    }

    @Override
    public Set<String> optionalArgs() {
        return Set.of(USERNAME.toString(), PASSWORD.toString(), SESSION_ID.toString());
    }

    @Override
    protected String unauthenticatedExecute(final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());
        final String password = args.get(PASSWORD.toString());
        final String sessionId = args.get(SESSION_ID.toString());
        try {
            boolean validArgs = false;
            String loggedSessionId = null;
            if (username != null && password != null && sessionId == null) {
                validArgs = true;
                loggedSessionId = userService.login(username, password);
            }
            if (username == null && password == null && sessionId != null) {
                validArgs = true;
                loggedSessionId = userService.login(sessionId);
            }
            if (!validArgs) {
                return "Invalid arguments.";
            }
            if (loggedSessionId == null) {
                return "No such session.";
            }
            currentSessionService.set(loggedSessionId);
            return loggedSessionId;
        } catch (InvalidUserDataException | InvalidUsernamePasswordCombination | IOException e) {
            return e.getMessage();
        }
    }

}
