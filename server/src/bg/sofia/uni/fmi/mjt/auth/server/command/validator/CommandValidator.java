package bg.sofia.uni.fmi.mjt.auth.server.command.validator;

import bg.sofia.uni.fmi.mjt.auth.server.command.base.Command;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.ParsedCommand;

public interface CommandValidator {

    void validate(final ParsedCommand parsedCommand, final Command command) throws InvalidCommandException;

}
