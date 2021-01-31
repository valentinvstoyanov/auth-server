package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.EMAIL_ARG;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.FIRST_NAME_ARG;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.LAST_NAME_ARG;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.PASSWORD_ARG;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME_ARG;

public class RegisterCommand extends Command {

    public static final String NAME = "register";

    private final UserService userService;

    public RegisterCommand(final UserService userService) {
        super(NAME);
        this.userService = userService;
    }

    @Override
    public Set<String> requiredArgs() {
        return Set.of(USERNAME_ARG.name(),
                PASSWORD_ARG.name(),
                FIRST_NAME_ARG.name(),
                LAST_NAME_ARG.name(),
                EMAIL_ARG.name());
    }

    @Override
    public Set<String> optionalArgs() {
        return null;
    }

    @Override
    public String execute(final Map<String, String> args) {
        final String username = args.get(USERNAME_ARG.name());
        final String password = args.get(PASSWORD_ARG.name());
        final String firstName = args.get(FIRST_NAME_ARG.name());
        final String lastName = args.get(LAST_NAME_ARG.name());
        final String email = args.get(EMAIL_ARG.name());
        try {
            return userService.register(username, password, firstName, lastName, email);
        } catch (UsernameAlreadyTakenException | InvalidUserDataException e) {
            return e.getMessage();
        }
    }

}
