package bg.sofia.uni.fmi.mjt.auth.server.request;

import bg.sofia.uni.fmi.mjt.auth.server.command.base.Command;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.CommandParseException;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.CommandParser;
import bg.sofia.uni.fmi.mjt.auth.server.command.parser.ParsedCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.validator.CommandValidator;
import bg.sofia.uni.fmi.mjt.auth.server.command.validator.InvalidCommandException;

import java.nio.channels.SocketChannel;
import java.util.Map;

public class CommandRequestHandler implements RequestHandler {

    private final CommandValidator commandValidator;
    private final CommandParser commandParser;
    private final Map<String, Command> commands;

    public CommandRequestHandler(final CommandValidator commandValidator,
                                 final CommandParser commandParser,
                                 final Map<String, Command> commands) {

        this.commandValidator = commandValidator;
        this.commandParser = commandParser;
        this.commands = commands;
    }

    @Override
    public String handle(final SocketChannel clientSocketChannel, final String request) {
        try {
            final ParsedCommand parsedCommand = commandParser.parse(request);
            final Command command = commands.get(parsedCommand.name());
            commandValidator.validate(parsedCommand, command);
            return command.execute(parsedCommand.args());
        } catch (CommandParseException | InvalidCommandException e) {
            return e.getMessage();
        }
    }

}
