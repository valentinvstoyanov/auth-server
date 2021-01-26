package bg.sofia.uni.fmi.mjt.auth.server.user.exception;

public class UsernameAlreadyTakenException extends Exception {

    private static final String MESSAGE_FORMAT = "%s is already taken";

    public UsernameAlreadyTakenException(final String username) {
        super(String.format(MESSAGE_FORMAT, username));
    }

}
