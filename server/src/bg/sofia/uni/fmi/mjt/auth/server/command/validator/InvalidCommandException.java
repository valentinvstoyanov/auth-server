package bg.sofia.uni.fmi.mjt.auth.server.command.validator;

public class InvalidCommandException extends Exception {

    public InvalidCommandException(final String message) {
        super(message);
    }

}
