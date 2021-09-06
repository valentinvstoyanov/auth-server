package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.AuthorizedCommand;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles.ADMIN;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class DeleteUserCommand extends AuthorizedCommand {

    public static final String NAME = "delete-user";
    public static final String DELETED_USER = "%s deleted.";
    public static final String CANNOT_DELETE_LAST_ADMIN = "Can't delete the last admin.";
    public static final String UNKNOWN_USERNAME = "Unknown username.";

    private final UserService userService;

    public DeleteUserCommand(final CurrentSessionIdService currentSessionIdService,
                                final AuthenticationService authenticationService,
                                final AuthorizationService authorizationService,
                                final UserService userService) {
        super(currentSessionIdService, authenticationService, authorizationService);
        this.userService = userService;
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
    protected String authorizedExecute(final SocketChannel clientSocketChannel, final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());

        if (authorizationService.authorize(username, ADMIN.role)) {
            if (authorizationService.roleCount(ADMIN.role) > 1) {
                return deleteUser(username);
            }
            return CANNOT_DELETE_LAST_ADMIN;
        }

        if (authorizationService.authorize(username, CommonRoles.AUTHENTICATED.role)) {
            return deleteUser(username);
        }

        return UNKNOWN_USERNAME;
    }

    private String deleteUser(final String username) {
        userService.deleteUserByUsername(username);
        authorizationService.remove(username);

        String deletedUserSessionId = authenticationService.getSessionIdByUsername(username);
        if (deletedUserSessionId != null) {
            authenticationService.invalidate(deletedUserSessionId);
        }

        return String.format(DELETED_USER, username);
    }

    @Override
    protected Role allowedRole() {
        return ADMIN.role;
    }

}
