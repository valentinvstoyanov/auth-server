package bg.sofia.uni.fmi.mjt.auth.server.command.parser;

import java.util.HashMap;
import java.util.Map;

public class NameArgsCommandParser implements CommandParser {

    private static final String DELIM = " ";
    private static final String MISSING_ARG_VALUE_FORMAT = "Missing value for %s argument.";
    private static final String MISSING_ARGUMENTS_MESSAGE = "Missing arguments";

    public ParsedCommand parse(final String commandStr) throws CommandParseException {
        final String[] nameArgs = commandStr.split(DELIM, 2);
        final String name = nameArgs[0];
        final String argsStr = nameArgs.length == 2 ? nameArgs[1] : null;
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
