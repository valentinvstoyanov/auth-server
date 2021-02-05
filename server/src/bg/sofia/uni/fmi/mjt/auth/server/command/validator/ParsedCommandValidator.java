package bg.sofia.uni.fmi.mjt.auth.server.command.validator;

import bg.sofia.uni.fmi.mjt.auth.server.command.base.Command;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.ParsedCommand;

import java.util.HashSet;
import java.util.Set;

public class ParsedCommandValidator implements CommandValidator {

    public static final String EXPECTS_NO_ARGS_FORMAT = "%s command expects no arguments.";
    public static final String UNKNOWN_COMMAND_FORMAT = "Unknown command: %s.";
    public static final String REQUIRED_ARGS_FORMAT = "Command requires %s arguments.";
    public static final String ARGS_DELIM = ", ";
    public static final String UNKNOWN_ARGUMETS_FORMAT = "Unknown argumets: %s.";

    public void validate(final ParsedCommand parsedCommand, final Command command) throws InvalidCommandException {
        if (command == null) {
            throw new InvalidCommandException(String.format(UNKNOWN_COMMAND_FORMAT, parsedCommand.name()));
        }

        final Set<String> parsedArgs = parsedCommand.args() == null ? null : parsedCommand.args().keySet();
        if (parsedArgs == null) {
            if (command.hasRequiredArgs()) {
                final String requiredArgsMessage =
                        String.format(REQUIRED_ARGS_FORMAT, joinArgs(command.requiredArgs()));
                throw new InvalidCommandException(requiredArgsMessage);
            }
            return;
        }

        if (command.hasRequiredArgs()) {
            final Set<String> parsedMinusRequiredArgs = checkMissingArgs(parsedArgs, command.requiredArgs());
            if (command.hasOptionalArgs()) {
                checkUnknownArgs(parsedMinusRequiredArgs, command.optionalArgs());
                return;
            }
            checkUnknownArgs(parsedMinusRequiredArgs, command.requiredArgs());
            return;
        }

        if (command.hasOptionalArgs()) {
            checkUnknownArgs(parsedArgs, command.optionalArgs());
            return;
        }

        throw new InvalidCommandException(String.format(EXPECTS_NO_ARGS_FORMAT, command.name()));
    }

    private Set<String> checkMissingArgs(final Set<String> parsedArgs,
                                         final Set<String> requiredArgs) throws InvalidCommandException {
        final Set<String> parsedMinusRequiredArgs = subtractArgs(parsedArgs, requiredArgs);
        if (parsedMinusRequiredArgs.size() + requiredArgs.size() != parsedArgs.size()) {
            final Set<String> missingRequiredArgs = subtractArgs(requiredArgs, parsedArgs);
            final String message = String.format(REQUIRED_ARGS_FORMAT, joinArgs(missingRequiredArgs));
            throw new InvalidCommandException(message);
        }
        return parsedMinusRequiredArgs;
    }

    private void checkUnknownArgs(final Set<String> parsedArgs,
                                  final Set<String> expectedArgs) throws InvalidCommandException {
        final Set<String> unknownArgs = subtractArgs(parsedArgs, expectedArgs);
        if (unknownArgs.size() > 0) {
            throw new InvalidCommandException(String.format(UNKNOWN_ARGUMETS_FORMAT, joinArgs(unknownArgs)));
        }
    }

    private Set<String> subtractArgs(final Set<String> a, final Set<String> b) {
        Set<String> result = new HashSet<>(a);
        result.removeIf(b::contains);
        return result;
    }

    private String joinArgs(final Set<String> args) {
        return String.join(ARGS_DELIM, args);
    }

}
