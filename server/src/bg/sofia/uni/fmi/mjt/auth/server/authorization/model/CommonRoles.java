package bg.sofia.uni.fmi.mjt.auth.server.authorization.model;

public enum CommonRoles {

    ADMIN("ADMIN"),
    AUTHENTICATED("AUTHENTICATED");

    public final Role role;

    CommonRoles(final String roleStr) {
        this.role = new Role(roleStr);
    }

}
