package bg.sofia.uni.fmi.mjt.auth.server.command.types.authenticated.admin;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Roles;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.command.types.authenticated.AuthenticatedCommand;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.maybedelete.CurrentSessionService;

import java.io.IOException;
import java.util.Map;

public abstract class AdminCommand extends AuthenticatedCommand {

    private final AuthorizationService authorizationService;

    protected AdminCommand(final AuthorizationService authorizationService,
                           final CurrentSessionService currentSessionService,
                           final SessionService sessionService) {

        super(currentSessionService, sessionService);
        this.authorizationService = authorizationService;
    }

    protected abstract String adminExecute(final Map<String, String> args);

    @Override
    protected String authenticatedExecute(final Map<String, String> args) {
        try {
            final String username = sessionService.getUsernameBySessionId(currentSessionService.get());
            boolean authorized = authorizationService.authorize(username, Roles.ADMIN.role);
            if (!authorized) {
                return "Unauthorized.";
            }
            return adminExecute(args);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return ioException.getMessage();
        }
    }

}
