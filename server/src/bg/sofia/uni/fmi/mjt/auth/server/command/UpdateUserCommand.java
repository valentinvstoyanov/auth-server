package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.AuthorizedCommand;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles.AUTHENTICATED;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.EMAIL;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.FIRST_NAME;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.LAST_NAME;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class UpdateUserCommand extends AuthorizedCommand {

    public static final String NAME = "update";
    private static final String SUCCESSFULLY_UPDATED = "User successfully updated.";
    private static final String NOTHING_TO_UPDATE = "Nothing to update.";
    public static final String CAN_ONLY_UPDATE_YOUR_OWN_DATA = "You can only update your own data.";

    private final UserService userService;

    public UpdateUserCommand(final CurrentSessionIdService currentSessionIdService,
                             final AuthenticationService authenticationService,
                             final AuthorizationService authorizationService,
                             final UserService userService) {
        super(currentSessionIdService, authenticationService, authorizationService);
        this.userService = userService;
    }


    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected Set<String> otherRequiredArgs() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> optionalArgs() {
        return Set.of(USERNAME.toString(), FIRST_NAME.toString(), LAST_NAME.toString(), EMAIL.toString());
    }

    @Override
    protected String authorizedExecute(final Map<String, String> args) {
        if (args.isEmpty()) {
            return NOTHING_TO_UPDATE;
        }

        final String sessionId = currentSessionIdService.get();
        final String sessionUsername = authenticationService.getUsernameBySessionId(sessionId);
        final String username = args.get(USERNAME.toString());
        final String firstName = args.get(FIRST_NAME.toString());
        final String lastName = args.get(LAST_NAME.toString());
        final String email = args.get(EMAIL.toString());
        try {
            final User user = userService.updateUser(sessionUsername,
                    username, firstName, lastName, email, null, null);
            if (!user.username().equals(sessionUsername)) {
                authenticationService.update(sessionId, user.username());
                currentSessionIdService.set(user.username());
                final Role role = authorizationService.remove(sessionUsername);
                authorizationService.assign(user.username(), role);
            }
            return SUCCESSFULLY_UPDATED;
        } catch (InvalidUserDataException | UsernameAlreadyTakenException | InvalidUsernamePasswordCombination e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    protected Role allowedRole() {
        return AUTHENTICATED.role;
    }

}
