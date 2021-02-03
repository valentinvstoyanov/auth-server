package bg.sofia.uni.fmi.mjt.auth.server.authorization.service;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;

public interface AuthorizationService {

    void setRole(String username, Role role);

    boolean authorize(String username, Role role);

}
