package bg.sofia.uni.fmi.mjt.auth.server.command;

import java.util.Map;
import java.util.Set;

public abstract class Command {

    private final String name;

    public abstract Set<String> requiredArgs();
    public abstract Set<String> optionalArgs();

    public abstract String execute(Map<String, String> args);

    protected Command(final String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public boolean hasRequiredArgs() {
        return requiredArgs() != null && !requiredArgs().isEmpty();
    }

    public boolean hasOptionalArgs() {
        return optionalArgs() != null && !optionalArgs().isEmpty();
    }

}
