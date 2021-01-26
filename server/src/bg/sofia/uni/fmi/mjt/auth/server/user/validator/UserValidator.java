package bg.sofia.uni.fmi.mjt.auth.server.user.validator;

import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;

public interface UserValidator {

    void validate(String username, String password, String firstName, String lastName, String email)
            throws InvalidUserDataException;

    void validate(String username, String password) throws InvalidUserDataException;

}
