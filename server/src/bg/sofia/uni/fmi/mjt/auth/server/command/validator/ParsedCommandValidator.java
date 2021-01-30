package bg.sofia.uni.fmi.mjt.auth.server.command.validator;

import bg.sofia.uni.fmi.mjt.auth.server.command.Command;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.ParsedCommand;

import java.util.HashSet;
import java.util.Set;

public class ParsedCommandValidator implements CommandValidator {

    public static final String COMMAND_EXPECTS_NO_ARGS_FORMAT = "%s command expects no arguments.";

    //TODO: check whether to split this function into multiple smaller ones
    //TODO: provide proper exception messages
    public void validate(final ParsedCommand parsedCommand, final Command command) throws InvalidCommandException {
        if (command == null) {
            throw new InvalidCommandException("");
        }

        if (parsedCommand.args() == null) {
            if (command.hasRequiredArgs()) {
                throw new InvalidCommandException("");
            } else {
                return;
            }
        }

        final Set<String> parsedArgs = parsedCommand.args().keySet();
        if (command.hasRequiredArgs()) {
            if (command.hasOptionalArgs()) {
                final Set<String> parsedOptionalArgs = new HashSet<>(parsedArgs);
                parsedOptionalArgs.removeAll(command.requiredArgs());
                if (!(parsedArgs.containsAll(command.requiredArgs()) && command.optionalArgs().containsAll(parsedOptionalArgs))) {
                    throw new InvalidCommandException("");
                }
            } else if (!parsedArgs.equals(command.requiredArgs())){
                throw new InvalidCommandException("");
            }
        } else if (command.hasOptionalArgs() && !command.optionalArgs().containsAll(parsedArgs)) {
            throw new InvalidCommandException("");
        } else {
            throw new InvalidCommandException(String.format(COMMAND_EXPECTS_NO_ARGS_FORMAT, command.name()));
        }
    }

}
