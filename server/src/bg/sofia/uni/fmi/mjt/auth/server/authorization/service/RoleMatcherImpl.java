package bg.sofia.uni.fmi.mjt.auth.server.authorization.service;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.CommonRoles;
import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;

public class RoleMatcherImpl implements RoleMatcher {

    @Override
    public boolean matchIsA(final Role role1, final Role role2) {
        if (role1.equals(role2)) {
            return true;
        }
        return role1.equals(CommonRoles.ADMIN.role) && role2.equals(CommonRoles.AUTHENTICATED.role);
    }

}
