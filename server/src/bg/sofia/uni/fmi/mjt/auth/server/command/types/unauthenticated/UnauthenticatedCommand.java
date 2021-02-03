package bg.sofia.uni.fmi.mjt.auth.server.command.types.unauthenticated;

import bg.sofia.uni.fmi.mjt.auth.server.command.Command;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.maybedelete.CurrentSessionService;

import java.util.Map;

public abstract class UnauthenticatedCommand extends Command {

    public static final String ALREADY_AUTHENTICATED = "You are already authenticated.";
    protected final CurrentSessionService currentSessionService;

    protected UnauthenticatedCommand(final CurrentSessionService currentSessionService) {
        this.currentSessionService = currentSessionService;
    }

    protected abstract String unauthenticatedExecute(final Map<String, String> args);

    @Override
    public String execute(final Map<String, String> args) {
        if (currentSessionService.get() != null) {
            return ALREADY_AUTHENTICATED;
        }
        return unauthenticatedExecute(args);
    }

}
