package bg.sofia.uni.fmi.mjt.auth.server.command.types.authenticated.admin;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.maybedelete.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class AddAdminCommand extends AdminCommand {

    public static final String NAME = "add-admin";
    public static final String SUCCESSFUL_ADMIN = "%s is now an admin.";

    private final UserService userService;

    public AddAdminCommand(final UserService userService,
                           final AuthorizationService authorizationService,
                           final CurrentSessionService currentSessionService,
                           final SessionService sessionService) {
        super(authorizationService, currentSessionService, sessionService);
        this.userService = userService;
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
    protected String adminExecute(final Map<String, String> args) {
        try {
            final String username = args.get(USERNAME.toString());
            userService.addAdmin(username);
            return String.format(SUCCESSFUL_ADMIN, username);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return ioException.getMessage();
        }
    }

}
