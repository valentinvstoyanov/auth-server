package bg.sofia.uni.fmi.mjt.auth.server.command.parser;

import java.util.Map;

public record ParsedCommand(String name, Map<String, String> args) {

    public ParsedCommand(final String name) {
        this(name, null);
    }

}
