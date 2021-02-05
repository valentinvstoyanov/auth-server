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

public class AddAdminCommand extends AuthorizedCommand {

    public static final String NAME = "add-admin";
    public static final String SUCCESSFUL_ADMIN = "%s is now an admin.";
    public static final String ALREADY_ADMIN = "%s is already admin.";

    public AddAdminCommand(final CurrentSessionIdService currentSessionIdService,
                              final AuthenticationService authenticationService, final AuthorizationService authorizationService) {
        super(currentSessionIdService, authenticationService, authorizationService);
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
    protected Set<String> otherRequiredArgs() {
        return Set.of(USERNAME.toString());
    }


    @Override
    protected String authorizedExecute(final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());
        if (authorizationService.authorize(username, ADMIN.role)) {
            return String.format(ALREADY_ADMIN, username);
        }

        authorizationService.assign(username, ADMIN.role);
        return String.format(SUCCESSFUL_ADMIN, username);
    }

    @Override
    protected Role allowedRole() {
        return ADMIN.role;
    }

}
