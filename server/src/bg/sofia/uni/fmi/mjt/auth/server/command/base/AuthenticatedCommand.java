package bg.sofia.uni.fmi.mjt.auth.server.command.base;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.SESSION_ID;

public abstract class AuthenticatedCommand extends Command {

    private static final String INVALID_SESSION_ID = "Your session id is invalid.";

    protected final CurrentSessionIdService currentSessionIdService;
    protected final AuthenticationService authenticationService;

    protected AuthenticatedCommand(final CurrentSessionIdService currentSessionIdService,
                                   final AuthenticationService authenticationService) {
        this.currentSessionIdService = currentSessionIdService;
        this.authenticationService = authenticationService;
    }

    protected abstract String authenticatedExecute(final Map<String, String> args);

    protected abstract Set<String> otherRequiredArgs();

    @Override
    public final Set<String> requiredArgs() {
        final Set<String> otherRequiredArgs = otherRequiredArgs();
        Set<String> args = otherRequiredArgs == null ? new HashSet<>() : new HashSet<>(otherRequiredArgs);
        args.add(SESSION_ID.toString());
        return args;
    }

    @Override
    public final String execute(final Map<String, String> args) {
        final String sessionId = args.get(SESSION_ID.toString());
        if (!authenticationService.validate(sessionId)) {
            currentSessionIdService.clear();
            return INVALID_SESSION_ID;
        }
        currentSessionIdService.set(sessionId);
        return authenticatedExecute(args);
    }

}
