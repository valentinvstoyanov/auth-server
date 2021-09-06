package bg.sofia.uni.fmi.mjt.auth.server.command;

import bg.sofia.uni.fmi.mjt.auth.server.audit.AuditLog;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.AuthenticationService;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session.CurrentSessionIdService;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.service.AuthorizationService;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.ConfigurationChangeCommand;
import bg.sofia.uni.fmi.mjt.auth.server.command.base.model.ConfigurationChangeResult;
import bg.sofia.uni.fmi.mjt.auth.server.ip.IpExtractor;

import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles.ADMIN;
import static bg.sofia.uni.fmi.mjt.auth.server.command.CommonArgs.USERNAME;

public class AddAdminCommand extends ConfigurationChangeCommand {

    public static final String NAME = "add-admin";
    public static final String SUCCESSFUL_ADMIN = "%s is now an admin.";
    public static final String ALREADY_ADMIN = "%s is already admin.";

    public AddAdminCommand(final CurrentSessionIdService currentSessionIdService,
                           final AuthenticationService authenticationService,
                           final AuthorizationService authorizationService,
                           final AuditLog auditLog, final IpExtractor ipExtractor) {
        super(currentSessionIdService, authenticationService, authorizationService, auditLog, ipExtractor);
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
    protected Set<String> otherRequiredArgs() {
        return Set.of(USERNAME.toString());
    }


    @Override
    protected String getAuthor(final Map<String, String> args) {
        return args.get(USERNAME.toString());
    }

    @Override
    protected ConfigurationChangeResult executeConfigurationChange(final Map<String, String> args) {
        final String username = args.get(USERNAME.toString());
        if (authorizationService.authorize(username, ADMIN.role)) {
            return new ConfigurationChangeResult(String.format(ALREADY_ADMIN, username), false);
        }

        authorizationService.assign(username, ADMIN.role);
        return new ConfigurationChangeResult(String.format(SUCCESSFUL_ADMIN, username), true);
    }

    @Override
    protected Role allowedRole() {
        return ADMIN.role;
    }

}
