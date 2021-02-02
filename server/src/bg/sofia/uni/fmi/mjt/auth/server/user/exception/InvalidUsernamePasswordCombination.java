package bg.sofia.uni.fmi.mjt.auth.server.user.exception;

public class InvalidUsernamePasswordCombination extends Exception {

    public static final String MESSAGE = "Invalid username or password combination";

    public InvalidUsernamePasswordCombination() {
        super(MESSAGE);
    }

}
