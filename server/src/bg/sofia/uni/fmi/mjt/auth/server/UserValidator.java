package bg.sofia.uni.fmi.mjt.auth.server;

public interface UserValidator {

    void validate(String username, String password, String firstName, String lastName, String email)
            throws InvalidUserDataException;

}
