package bg.sofia.uni.fmi.mjt.auth.server.command.types.authenticated;

import bg.sofia.uni.fmi.mjt.auth.server.session.service.maybedelete.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.NEW_PASSWORD;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.OLD_PASSWORD;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class UpdatePasswordCommand extends AuthenticatedCommand {

    public static final String NAME = "reset-password";
    public static final String CHANGE_OTHER_USERNAMES = "Cannot change other user passwords.";
    public static final String SUCCESSFULLY_UPDATED = "Password successfully updated.";

    private final UserService userService;

    public UpdatePasswordCommand(final CurrentSessionService currentSessionService,
                                 final SessionService sessionService,
                                 final UserService userService) {
        super(currentSessionService, sessionService);
        this.userService = userService;
    }

    @Override
    protected String authenticatedExecute(final Map<String, String> args) {
        try {
            final String username = args.get(USERNAME.toString());
            final String oldPassword = args.get(OLD_PASSWORD.toString());
            final String newPassword = args.get(NEW_PASSWORD.toString());
            final String sessionId = currentSessionService.get();
            final String currentSessionUsername = sessionService.getUsernameBySessionId(sessionId);
            if (!username.equals(currentSessionUsername)) {
                return CHANGE_OTHER_USERNAMES;
            }
            userService.update(sessionId, null, null, null, null, oldPassword, newPassword);
            return SUCCESSFULLY_UPDATED;
        } catch (IOException | InvalidUsernamePasswordCombination | InvalidUserDataException | UsernameAlreadyTakenException e) {
            e.printStackTrace();
            return e.getMessage();
        }
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
