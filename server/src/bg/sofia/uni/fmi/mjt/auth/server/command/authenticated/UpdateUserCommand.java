package bg.sofia.uni.fmi.mjt.auth.server.command.authenticated;

import bg.sofia.uni.fmi.mjt.auth.server.session.service.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.EMAIL;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.FIRST_NAME;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.LAST_NAME;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class UpdateUserCommand extends AuthenticatedCommand {

    public static final String NAME = "update";
    private static final String SUCCESSFULLY_UPDATED = "User successfully updated.";

    private final UserService userService;

    public UpdateUserCommand(final CurrentSessionService currentSessionService,
                             final SessionService sessionService,
                             final UserService userService) {
        super(currentSessionService, sessionService);
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
    protected String authenticatedExecute(final Map<String, String> args) {
        try {
            final String sessionId = currentSessionService.get();
            final String username = args.get(USERNAME.toString());
            final String firstName = args.get(FIRST_NAME.toString());
            final String lastName = args.get(LAST_NAME.toString());
            final String email = args.get(EMAIL.toString());
            userService.update(sessionId, username, firstName, lastName, email, null, null);
            return SUCCESSFULLY_UPDATED;
        } catch (IOException | InvalidUsernamePasswordCombination | InvalidUserDataException | UsernameAlreadyTakenException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
