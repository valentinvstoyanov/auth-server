package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.PASSWORD;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class LoginCommand extends Command {

    public static final String NAME = "login";

    private final UserService userService;

    public LoginCommand(final UserService userService) {
        super(NAME);
        this.userService = userService;
    }

    @Override
    public Set<String> requiredArgs() {
        return Set.of(USERNAME.argName, PASSWORD.argName);
    }

    @Override
    public Set<String> optionalArgs() {
        return null;
    }

    @Override
    public String execute(final Map<String, String> args) {
        final String username = args.get(USERNAME.argName);
        final String password = args.get(PASSWORD.argName);
        try {
            return userService.login(username, password);
        } catch (InvalidUserDataException | InvalidUsernamePasswordCombination | IOException e) {
            return e.getMessage();
        }
    }

}
