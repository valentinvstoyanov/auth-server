package bg.sofia.uni.fmi.mjt.auth.server.command.parser;

import java.util.HashMap;
import java.util.Map;

public class NameArgsCommandParser implements CommandParser {

    private static final String DELIM = " ";
    private static final String MISSING_ARG_VALUE_FORMAT = "Missing value for %s argument.";
    public static final int NAME_INDEX = 0;
    public static final int ARGS_INDEX = 1;
    public static final int COMMAND_PARTS = 2;

    public ParsedCommand parse(final String commandStr) throws CommandParseException {
        final String[] nameArgs = commandStr.split(DELIM, COMMAND_PARTS);
        final String name = nameArgs[NAME_INDEX];
        final String argsStr = nameArgs.length == COMMAND_PARTS ? nameArgs[ARGS_INDEX] : null;
        final Map<String, String> args = parseArgs(argsStr);
        return new ParsedCommand(name, args);
    }

    private Map<String, String> parseArgs(final String args) throws CommandParseException {
        if (args == null || args.isEmpty()) {
            return null;
        }

        final String[] tokens = args.split(DELIM);
        if (tokens.length % 2 != 0) {
            final String argName = tokens[tokens.length - 1];
            final String message = String.format(MISSING_ARG_VALUE_FORMAT, argName);
            throw new CommandParseException(message);
        }

        final Map<String, String> argValueMap = new HashMap<>();
        for (int i = 1; i < tokens.length; i += 2) {
            argValueMap.put(tokens[i - 1], tokens[i]);
        }
        return argValueMap;
    }

}
