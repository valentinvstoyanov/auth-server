package bg.sofia.uni.fmi.mjt.auth.server.command.base;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

public abstract class Command {

    public abstract String name();

    public abstract Set<String> requiredArgs();
    public abstract Set<String> optionalArgs();

    public abstract String execute(SocketChannel clientSocketChannel, Map<String, String> args);

    public final boolean hasRequiredArgs() {
        return requiredArgs() != null && !requiredArgs().isEmpty();
    }

    public final boolean hasOptionalArgs() {
        return optionalArgs() != null && !optionalArgs().isEmpty();
    }

}
