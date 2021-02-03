package bg.sofia.uni.fmi.mjt.auth.server.command.types.authenticated.admin;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.maybedelete.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class RemoveAdminCommand extends AdminCommand {

    public static final String NAME = "remove-admin";
    public static final String FAILED_REMOVE_ADMIN = "Failed to remove admin %s.";
    public static final String SUCCESS_REMOVING_ADMIN = "%s is no longer an admin.";
    private final UserService userService;

    public RemoveAdminCommand(final AuthorizationService authorizationService,
                              final CurrentSessionService currentSessionService,
                              final SessionService sessionService, final UserService userService) {
        super(authorizationService, currentSessionService, sessionService);
        this.userService = userService;
    }

    @Override
    protected String adminExecute(final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());
        try {
            if (!userService.removeAdmin(username)) {
                return String.format(FAILED_REMOVE_ADMIN, username);
            }
            return String.format(SUCCESS_REMOVING_ADMIN, username);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return ioException.getMessage();
        }
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

}
