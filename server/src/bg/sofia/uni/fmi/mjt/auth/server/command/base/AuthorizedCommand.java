package bg.sofia.uni.fmi.mjt.auth.server.command.base;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;

import java.util.Map;

public abstract class AuthorizedCommand extends AuthenticatedCommand {

    public static final String UNAUTHORIZED = "Unauthorized.";

    protected final AuthorizationService authorizationService;

    protected AuthorizedCommand(final CurrentSessionIdService currentSessionIdService,
                                final AuthenticationService authenticationService,
                                final AuthorizationService authorizationService) {
        super(currentSessionIdService, authenticationService);
        this.authorizationService = authorizationService;
    }

    protected abstract String authorizedExecute(Map<String, String> args);

    protected abstract Role allowedRole();

    @Override
    protected final String authenticatedExecute(final Map<String, String> args) {
        final String sessionId = currentSessionIdService.get();
        final String username = authenticationService.getUsernameBySessionId(sessionId);
        if (authorizationService.authorize(username, allowedRole())) {
            return authorizedExecute(args);
        }
        return UNAUTHORIZED;
    }

}
