package bg.sofia.uni.fmi.mjt.auth.server.authorization.repository;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Authorization;

import java.util.Collection;

public interface AuthorizationRepository {

    Authorization createAuthorization(Authorization authorization);

    Authorization updateAuthorizationUsername(String oldUsername, Authorization authorization);

    Authorization deleteAuthorizationByUsername(String username);

    Authorization getAuthorizationByUsername(String username);

    Collection<Authorization> getAllAuthorizations();

}
