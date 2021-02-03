package bg.sofia.uni.fmi.mjt.auth.server.authorization.model;

public enum Roles {

    ADMIN("ADMIN"),
    AUTHENTICATED("AUTHENTICATED");

    public final Role role;

    Roles(final String roleStr) {
        this.role = new Role(roleStr);
    }

}
