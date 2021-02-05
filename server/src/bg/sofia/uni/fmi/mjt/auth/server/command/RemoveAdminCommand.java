package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.AuthorizedCommand;

import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles.ADMIN;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class RemoveAdminCommand extends AuthorizedCommand {

    public static final String NAME = "remove-admin";
    public static final String NO_LONGER_ADMIN = "%s is no longer admin.";
    public static final String NOT_ADMIN = "%s is not admin.";
    public static final String REMOVE_THE_LAST_ADMIN = "Can't remove the only admin in the server.";

    public RemoveAdminCommand(final CurrentSessionIdService currentSessionIdService,
                                 final AuthenticationService authenticationService,
                                 final AuthorizationService authorizationService) {
        super(currentSessionIdService, authenticationService, authorizationService);
    }

    @Override
    protected Set<String> otherRequiredArgs() {
        return Set.of(USERNAME.toString());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Set<String> optionalArgs() {
        return null;
    }

    @Override
    protected String authorizedExecute(final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());
        if (authorizationService.authorize(username, ADMIN.role)) {
            if (authorizationService.roleCount(ADMIN.role) > 1) {
                authorizationService.assign(username, CommonRoles.AUTHENTICATED.role);
                return String.format(NO_LONGER_ADMIN, username);
            }
            return REMOVE_THE_LAST_ADMIN;
        }
        return String.format(NOT_ADMIN, username);
    }

    @Override
    protected Role allowedRole() {
        return ADMIN.role;
    }

}
