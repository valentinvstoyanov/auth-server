package bg.sofia.uni.fmi.mjt.auth.server.command.types.authenticated.admin;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.maybedelete.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class DeleteUserCommand extends AdminCommand {

    public static final String NAME = "delete-user";
    public static final String DELETED_USER = "%s deleted.";
    private final UserService userService;

    public DeleteUserCommand(final AuthorizationService authorizationService, final CurrentSessionService currentSessionService, final SessionService sessionService, final UserService userService) {
        super(authorizationService, currentSessionService, sessionService);
        this.userService = userService;
    }

    @Override
    protected String adminExecute(final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());
        try {
            userService.delete(username);
            return String.format(DELETED_USER, username);
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
