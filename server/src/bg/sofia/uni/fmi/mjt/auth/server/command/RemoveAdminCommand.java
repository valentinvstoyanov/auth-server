package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.audit.AuditLog;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.ConfigurationChangeCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.model.ConfigurationChangeResult;
import bg.sofia.uni.fmi.mjt.auth.server.ip.IpExtractor;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles.ADMIN;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class RemoveAdminCommand extends ConfigurationChangeCommand {

    public static final String NAME = "remove-admin";
    public static final String NO_LONGER_ADMIN = "%s is no longer admin.";
    public static final String NOT_ADMIN = "%s is not admin.";
    public static final String REMOVE_THE_LAST_ADMIN = "Can't remove the only admin in the server.";

    public RemoveAdminCommand(final CurrentSessionIdService currentSessionIdService,
                              final AuthenticationService authenticationService,
                              final AuthorizationService authorizationService,
                              final AuditLog auditLog,
                              final IpExtractor ipExtractor) {
        super(currentSessionIdService, authenticationService, authorizationService, auditLog, ipExtractor);
    }

    @Override
    protected Set<String> otherRequiredArgs() {
        return Set.of(USERNAME.toString());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Set<String> optionalArgs() {
        return null;
    }

    @Override
    protected String getAuthor(final Map<String, String> args) {
        return args.get(USERNAME.toString());
    }

    @Override
    protected ConfigurationChangeResult executeConfigurationChange(final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());
        if (authorizationService.authorize(username, ADMIN.role)) {
            if (authorizationService.roleCount(ADMIN.role) > 1) {
                authorizationService.assign(username, CommonRoles.AUTHENTICATED.role);
                return new ConfigurationChangeResult(String.format(NO_LONGER_ADMIN, username), true);
            }
            return new ConfigurationChangeResult(REMOVE_THE_LAST_ADMIN, false);
        }
        return new ConfigurationChangeResult(String.format(NOT_ADMIN, username), false);
    }

    @Override
    protected Role allowedRole() {
        return ADMIN.role;
    }

}
