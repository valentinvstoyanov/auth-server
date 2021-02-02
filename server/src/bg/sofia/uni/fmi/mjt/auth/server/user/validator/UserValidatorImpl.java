package bg.sofia.uni.fmi.mjt.auth.server.user.validator;

import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;

public class UserValidatorImpl implements UserValidator {

    @Override
    public void validate(final String username,
                         final String password,
                         final String firstName,
                         final String lastName,
                         final String email)
            throws InvalidUserDataException {

        //TODO
    }

    @Override
    public void validate(final String username, final String firstName, final String lastName, final String email) throws InvalidUserDataException {
        //TODO
    }

    @Override
    public void validate(final String username, final String password) throws InvalidUserDataException {
        //TODO
    }

}
