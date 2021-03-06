package bg.sofia.uni.fmi.mjt.auth.server.command;

public enum CommonArgs {
    USERNAME("-username"),
    PASSWORD("-password"),
    FIRST_NAME("-first-name"),
    LAST_NAME("-last-name"),
    EMAIL("-email"),
    OLD_PASSWORD("-old-password"),
    NEW_PASSWORD("-new-password"),
    SESSION_ID("-session-id");

    private final String argName;

    CommonArgs(final String argName) {
        this.argName = argName;
    }

    @Override
    public String toString() {
        return argName;
    }

}
