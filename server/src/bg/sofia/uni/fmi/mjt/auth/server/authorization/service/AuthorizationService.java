package bg.sofia.uni.fmi.mjt.auth.server.authorization.service;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;

public interface AuthorizationService {

    Role assign(String username, Role aRole);

    Role remove(String username);

    boolean authorize(String username, Role role);

    long roleCount(Role role);

}
