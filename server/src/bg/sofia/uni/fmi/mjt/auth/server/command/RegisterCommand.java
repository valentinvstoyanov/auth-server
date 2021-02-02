package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.EMAIL;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.FIRST_NAME;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.LAST_NAME;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.PASSWORD;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class RegisterCommand extends Command {

    public static final String NAME = "register";

    private final UserService userService;

    public RegisterCommand(final UserService userService) {
        super(NAME);
        this.userService = userService;
    }

    @Override
    public Set<String> requiredArgs() {
        return Set.of(USERNAME.argName,
                PASSWORD.argName,
                FIRST_NAME.argName,
                LAST_NAME.argName,
                EMAIL.argName);
    }

    @Override
    public Set<String> optionalArgs() {
        return null;
    }

    @Override
    public String execute(final Map<String, String> args) {
        final String username = args.get(USERNAME.argName);
        final String password = args.get(PASSWORD.argName);
        final String firstName = args.get(FIRST_NAME.argName);
        final String lastName = args.get(LAST_NAME.argName);
        final String email = args.get(EMAIL.argName);
        try {
            return userService.register(username, password, firstName, lastName, email);
        } catch (UsernameAlreadyTakenException | InvalidUserDataException | IOException e) {
            return e.getMessage();
        } finally {
        }
    }

}
