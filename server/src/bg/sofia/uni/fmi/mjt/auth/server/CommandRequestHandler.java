package bg.sofia.uni.fmi.mjt.auth.server;

import bg.sofia.uni.fmi.mjt.auth.server.command.Command;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.CommandParseException;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.CommandParser;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.ParsedCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.NameArgsCommandParser;
import bg.sofia.uni.fmi.mjt.auth.server.command.validator.CommandValidator;
import bg.sofia.uni.fmi.mjt.auth.server.command.validator.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.auth.server.command.validator.ParsedCommandValidator;

import java.util.Map;

public class CommandRequestHandler implements RequestHandler {

    private final CommandValidator parsedCommandValidator;
    private final CommandParser commandParser;
    private final Map<String, Command> commands;

    public CommandRequestHandler(final ParsedCommandValidator parsedCommandValidator,
                                 final NameArgsCommandParser commandParser,
                                 final Map<String, Command> commands) {

        this.parsedCommandValidator = parsedCommandValidator;
        this.commandParser = commandParser;
        this.commands = commands;
    }

    @Override
    public String handle(final String request) {
        try {
            final ParsedCommand parsedCommand = commandParser.parse(request);
            final Command command = commands.get(parsedCommand.name());
            parsedCommandValidator.validate(parsedCommand, command);
            return command.execute(parsedCommand.args());
        } catch (CommandParseException | InvalidCommandException e) {
            return e.getMessage();
        }
    }

}
