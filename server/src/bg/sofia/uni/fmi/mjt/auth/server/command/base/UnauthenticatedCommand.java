package bg.sofia.uni.fmi.mjt.auth.server.command.base;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;

import java.nio.channels.SocketChannel;
import java.util.Map;

public abstract class UnauthenticatedCommand extends Command {

    public static final String ALREADY_AUTHENTICATED = "You are already authenticated.";

    protected final CurrentSessionIdService currentSessionIdService;

    protected UnauthenticatedCommand(final CurrentSessionIdService currentSessionIdService) {
        this.currentSessionIdService = currentSessionIdService;
    }

    protected abstract String unauthenticatedExecute(final SocketChannel clientSocketChannel, final Map<String, String> args);

    @Override
    public String execute(final SocketChannel clientSocketChannel, final Map<String, String> args) {
        if (currentSessionIdService.get() != null) {
            return ALREADY_AUTHENTICATED;
        }
        return unauthenticatedExecute(clientSocketChannel,args);
    }

}
