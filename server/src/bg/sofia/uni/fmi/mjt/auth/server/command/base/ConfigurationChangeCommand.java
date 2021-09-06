package bg.sofia.uni.fmi.mjt.auth.server.command.base;

import bg.sofia.uni.fmi.mjt.auth.server.audit.AuditLog;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.model.ConfigurationChangeResult;
import bg.sofia.uni.fmi.mjt.auth.server.ip.IpExtractor;

import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ConfigurationChangeCommand extends AuthorizedCommand {

    private static final String CONFIG_CHANGE_TAG = "config-change";
    private static final String CONFIGURATION_CHANGE_STARTED_MESSAGE = "Timestamp: %s, ID: %d, User: %s, IP: %s, Action: %s";
    private static final String CONFIGURATION_CHANGE_ENDED_MESSAGE = "Timestamp: %s, ID: %d, User: %s, IP: %s, %s";
    private static final String CONFIGURATION_CHANGE_SUCCEEDED = "SUCCEEDED";
    private static final String CONFIGURATION_CHANGE_FAILED = "FAILED";

    private final AuditLog auditLog;

    private final IpExtractor ipExtractor;

    private final AtomicInteger nextId;

    protected ConfigurationChangeCommand(final CurrentSessionIdService currentSessionIdService,
                                         final AuthenticationService authenticationService,
                                         final AuthorizationService authorizationService,
                                         final AuditLog auditLog,
                                         final IpExtractor ipExtractor) {
        super(currentSessionIdService, authenticationService, authorizationService);
        this.auditLog = auditLog;
        this.ipExtractor = ipExtractor;
        this.nextId = new AtomicInteger(0);
    }

    protected abstract String getAuthor(Map<String, String> args);

    protected abstract ConfigurationChangeResult executeConfigurationChange(Map<String, String> args);

    @Override
    protected String authorizedExecute(final SocketChannel clientSocketChannel, final Map<String, String> args) {
        final int id = nextId.getAndIncrement();
        final String ip = ipExtractor.extract(clientSocketChannel);
        final String author = getAuthor(args);

        logConfigurationChangeStarted(id, ip, author);
        final ConfigurationChangeResult result = executeConfigurationChange(args);
        logConfigurationChangeEnded(id, ip, author, result.isSuccessful() ? CONFIGURATION_CHANGE_SUCCEEDED : CONFIGURATION_CHANGE_FAILED);

        return result.message();
    }

    private void logConfigurationChangeStarted(final int id, final String ip, final String author) {
        final String message = String.format(CONFIGURATION_CHANGE_STARTED_MESSAGE, LocalDateTime.now().toString(), id, author, ip, name());
        auditLog.log(CONFIG_CHANGE_TAG, message);
    }

    private void logConfigurationChangeEnded(final int id, final String ip, final String author, final String successful) {
        final String message = String.format(CONFIGURATION_CHANGE_ENDED_MESSAGE, LocalDateTime.now().toString(), id, author, ip, successful);
        auditLog.log(CONFIG_CHANGE_TAG, message);
    }

}
