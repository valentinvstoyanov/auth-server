package bg.sofia.uni.fmi.mjt.auth.server.authorization.service;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Authorization;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.repository.AuthorizationRepository;

public class AuthorizationServiceImpl implements AuthorizationService {

    private final AuthorizationRepository authorizationRepository;
    private final RoleMatcher roleMatcher;

    public AuthorizationServiceImpl(final AuthorizationRepository authorizationRepository,
                                    final RoleMatcher roleMatcher) {
        this.authorizationRepository = authorizationRepository;
        this.roleMatcher = roleMatcher;
    }

    @Override
    public Role assign(final String username, final Role aRole) {
        final Authorization authorization = new Authorization(username, aRole);
        return authorizationRepository.createAuthorization(authorization).role();
    }

    @Override
    public boolean authorize(final String username, final Role role) {
        final Authorization authorization = authorizationRepository.getAuthorizationByUsername(username);
        return authorization != null && roleMatcher.matchIsA(authorization.role(), role);
    }

    @Override
    public Role remove(final String username) {
        final Authorization authorization = authorizationRepository.deleteAuthorizationByUsername(username);
        return authorization == null ? null : authorization.role();
    }

    @Override
    public long roleCount(final Role role) {
        return authorizationRepository.getAllAuthorizations()
                .stream()
                .filter(authorization -> roleMatcher.matchIsA(authorization.role(), role))
                .count();
    }

}
