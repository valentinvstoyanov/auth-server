package bg.sofia.uni.fmi.mjt.auth.server.command;

public enum CommonArgs {
    USERNAME("-username"),
    PASSWORD("-password"),
    FIRST_NAME("-first-name"),
    LAST_NAME("-last-name"),
    EMAIL("-email"),
    SESSION_ID_ARG("-session-id");

    public final String argName;

    CommonArgs(final String argName) {
        this.argName = argName;
    }
}
