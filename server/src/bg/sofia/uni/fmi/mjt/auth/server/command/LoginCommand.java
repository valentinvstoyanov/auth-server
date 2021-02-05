package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.UnauthenticatedCommand;

import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.PASSWORD;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.SESSION_ID;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class LoginCommand extends UnauthenticatedCommand {

    public static final String NAME = "login";
    public static final String AUTHENTICATION_FAILED = "Authentication failed.";
    public static final String INVALID_ARGUMENTS_COMBINATION = "Invalid arguments combination.";

    private final AuthenticationService authenticationService;

    public LoginCommand(final CurrentSessionIdService currentSessionIdService,
                        final AuthenticationService authenticationService) {
        super(currentSessionIdService);
        this.authenticationService = authenticationService;
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
        if (!validateArgs(username, password, sessionId)) {
            return INVALID_ARGUMENTS_COMBINATION;
        }

        final String authenticatedSessionId = authenticate(username, password, sessionId);
        if (authenticatedSessionId == null) {
            return AUTHENTICATION_FAILED;
        }

        currentSessionIdService.set(authenticatedSessionId);
        return authenticatedSessionId;

    }

    private String authenticate(final String username, final String password, final String sessionId) {
        if (sessionId == null) {
            return authenticationService.authenticate(username, password);
        } else {
            return authenticationService.refresh(sessionId);
        }
    }

    private boolean validateArgs(final String username, final String password, final String sessionId) {
        if (username != null && password != null && sessionId == null) {
            return true;
        }
        return username == null && password == null && sessionId != null;
    }

}
