package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.AuthenticatedCommand;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

public class LogoutCommand extends AuthenticatedCommand {

    public static final String NAME = "logout";

    public static final String SUCCESSFUL_LOGOUT = "Bye, bye.";
    public static final String FAILED_LOGOUT = "Logout failed.";

    public LogoutCommand(final CurrentSessionIdService currentSessionIdService,
                         final AuthenticationService authenticationService) {
        super(currentSessionIdService, authenticationService);
    }


    @Override
    protected String authenticatedExecute(final SocketChannel clientSocketChannel, final Map<String, String> args) {
        final String sessionId = currentSessionIdService.get();
        if (!authenticationService.invalidate(sessionId)) {
            return FAILED_LOGOUT;
        }
        currentSessionIdService.clear();
        return SUCCESSFUL_LOGOUT;
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
