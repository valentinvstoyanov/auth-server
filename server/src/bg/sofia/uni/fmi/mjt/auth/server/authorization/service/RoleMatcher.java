package bg.sofia.uni.fmi.mjt.auth.server.authorization.service;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;

public interface RoleMatcher {

    //r1 equals r2 or r1 isA r2, ex. admin isA authenticated
    boolean matchIsA(Role r1, Role r2);

}
