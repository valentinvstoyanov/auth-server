package bg.sofia.uni.fmi.mjt.auth.server.authorization.repository;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Authorization;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.KeyValueDataStore;

import java.util.Collection;
import java.util.stream.Collectors;

public class KeyValueAuthorizationRepository implements AuthorizationRepository {

    private final KeyValueDataStore<String, Role> rolesStore;
    private final KeyValueDataStore<String, Role> rolesCache;

    public KeyValueAuthorizationRepository(final KeyValueDataStore<String, Role> rolesStore,
                                           final KeyValueDataStore<String, Role> rolesCache) {
        this.rolesStore = rolesStore;
        this.rolesCache = rolesCache;
        initRolesCache();
    }

    private void initRolesCache() {
        for (final var entry : rolesStore.getAll().entrySet()) {
            rolesCache.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Authorization createAuthorization(final Authorization authorization) {
        rolesStore.put(authorization.username(), authorization.role());
        rolesCache.put(authorization.username(), authorization.role());
        return authorization;
    }

    @Override
    public Authorization updateAuthorizationUsername(final String oldUsername, final Authorization authorization) {
        if (deleteAuthorizationByUsername(oldUsername) == null) {
            return null;
        }
        return createAuthorization(authorization);
    }

    @Override
    public Authorization deleteAuthorizationByUsername(final String username) {
        rolesCache.deleteByKey(username);
        final Role role = rolesStore.deleteByKey(username);
        return new Authorization(username, role);
    }

    @Override
    public Authorization getAuthorizationByUsername(final String username) {
        return new Authorization(username, rolesCache.getByKey(username));
    }

    @Override
    public Collection<Authorization> getAllAuthorizations() {
        return rolesCache.getAll()
                .entrySet()
                .stream()
                .map(entry -> new Authorization(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
