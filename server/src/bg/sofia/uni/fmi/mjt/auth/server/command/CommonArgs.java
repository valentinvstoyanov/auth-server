package bg.sofia.uni.fmi.mjt.auth.server.command;

public enum CommonArgs {
    USERNAME_ARG("-username"),
    PASSWORD_ARG("-password"),
    FIRST_NAME_ARG("-first-name"),
    LAST_NAME_ARG("-last-name"),
    EMAIL_ARG("-email"),
    SESSION_ID_ARG("-session-id");

    private final String name;

    CommonArgs(final String name) {
        this.name = name;
    }
}
