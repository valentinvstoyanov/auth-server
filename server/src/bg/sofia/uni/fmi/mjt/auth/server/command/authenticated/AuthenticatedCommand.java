package bg.sofia.uni.fmi.mjt.auth.server.command.authenticated;

import bg.sofia.uni.fmi.mjt.auth.server.command.Command;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.CurrentSessionService;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.SESSION_ID;

public abstract class AuthenticatedCommand extends Command {

    private static final String UNAUTHENTICATED = "You are unauthenticated.";
    private static final String EXPIRED_SESSION = "Your session has expired.";

    protected final CurrentSessionService currentSessionService;
    protected final SessionService sessionService;

    protected AuthenticatedCommand(final CurrentSessionService currentSessionService,
                                   final SessionService sessionService) {
        this.currentSessionService = currentSessionService;
        this.sessionService = sessionService;
    }

    protected abstract String authenticatedExecute(final Map<String, String> args);
    protected abstract Set<String> otherRequiredArgs();

    @Override
    public final Set<String> requiredArgs() {
        Set<String> args = new HashSet<>(otherRequiredArgs());
        args.add(SESSION_ID.toString());
        return args;
    }

    @Override
    public final String execute(final Map<String, String> args) {
        final String argSessionId = args.get(SESSION_ID.toString());
        final String currentSessionId = currentSessionService.get();
        if (!argSessionId.equals(currentSessionId)) {
            return UNAUTHENTICATED;
        }

        try {
            if (sessionService.getUsernameBySessionId(currentSessionId) == null) {
                return EXPIRED_SESSION;
            }
            return authenticatedExecute(args);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return ioException.getMessage();
        }
    }

}
