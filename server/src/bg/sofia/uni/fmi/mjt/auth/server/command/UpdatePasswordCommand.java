package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.AuthorizedCommand;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles.AUTHENTICATED;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.NEW_PASSWORD;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.OLD_PASSWORD;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class UpdatePasswordCommand extends AuthorizedCommand {

    public static final String NAME = "reset-password";
    public static final String SUCCESSFULLY_UPDATED = "Password successfully updated.";
    public static final String YOU_ONLY_CHANGE_YOUR_PASSWORD = "You only change your password.";

    private final UserService userService;

    public UpdatePasswordCommand(final CurrentSessionIdService currentSessionIdService,
                                    final AuthenticationService authenticationService,
                                    final AuthorizationService authorizationService,
                                    final UserService userService) {
        super(currentSessionIdService, authenticationService, authorizationService);
        this.userService = userService;
    }


    @Override
    protected String authorizedExecute(final SocketChannel clientSocketChannel, final Map<String, String> args) {
        final String sessionId = currentSessionIdService.get();
        final String sessionUsername = authenticationService.getUsernameBySessionId(sessionId);
        final String username = args.get(USERNAME.toString());
        if (sessionUsername.equals(username)) {
            final String oldPassword = args.get(OLD_PASSWORD.toString());
            final String newPassword = args.get(NEW_PASSWORD.toString());
            try {
                userService.updateUser(sessionUsername, null, null, null, null, oldPassword, newPassword);
                return SUCCESSFULLY_UPDATED;
            } catch (InvalidUserDataException | UsernameAlreadyTakenException | InvalidUsernamePasswordCombination e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
        return YOU_ONLY_CHANGE_YOUR_PASSWORD;
    }

    @Override
    protected Role allowedRole() {
        return AUTHENTICATED.role;
    }

    @Override
    protected Set<String> otherRequiredArgs() {
        return Set.of(USERNAME.toString(), OLD_PASSWORD.toString(), NEW_PASSWORD.toString());
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
