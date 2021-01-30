package bg.sofia.uni.fmi.mjt.auth.server.command.parser;

public interface CommandParser {

    ParsedCommand parse(final String commandStr) throws CommandParseException;

}
