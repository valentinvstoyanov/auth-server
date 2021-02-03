package bg.sofia.uni.fmi.mjt.auth.server.command.types.authenticated;

import bg.sofia.uni.fmi.mjt.auth.server.session.service.maybedelete.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class LogoutCommand extends AuthenticatedCommand {

    public static final String NAME = "logout";
    public static final String SUCCESSFUL_LOGOUT = "Bye bye!";
    public static final String FAILED_LOGOUT = "Logout failed.";

    private final UserService userService;

    public LogoutCommand(final CurrentSessionService currentSessionService,
                         final SessionService sessionService,
                         final UserService userService) {
        super(currentSessionService, sessionService);
        this.userService = userService;
    }

    @Override
    protected String authenticatedExecute(final Map<String, String> args) {
        try {
            if (!userService.logout(currentSessionService.get())) {
                return FAILED_LOGOUT;
            }
            currentSessionService.set(null);
            return SUCCESSFUL_LOGOUT;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return ioException.getMessage();
        }
    }

    @Override
    protected Set<String> otherRequiredArgs() {
        return null;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Set<String> optionalArgs() {
        return null;
    }

}
