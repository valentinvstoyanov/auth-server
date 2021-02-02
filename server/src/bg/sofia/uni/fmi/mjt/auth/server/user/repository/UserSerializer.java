package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.storage.Serializer;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;

import java.util.Collection;
import java.util.List;

public class UserSerializer implements Serializer<User> {

    private static final String FIELD_DELIM = " ";
    private static final int USERNAME_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;
    private static final int FIRST_NAME_INDEX = 2;
    private static final int LAST_NAME_INDEX = 3;
    private static final int EMAIL_INDEX = 4;

    @Override
    public String serialize(final User user) {
        final Collection<String> fieldValues = List.of(user.username(),
                user.password(),
                user.firstName(),
                user.lastName(),
                user.email());
        return String.join(FIELD_DELIM, fieldValues);
    }

    @Override
    public User deserialize(final String str) {
        final String[] values = str.split(FIELD_DELIM);
        final String username = values[USERNAME_INDEX];
        final String password = values[PASSWORD_INDEX];
        final String firstName = values[FIRST_NAME_INDEX];
        final String lastName = values[LAST_NAME_INDEX];
        final String email = values[EMAIL_INDEX];
        return new User(username, password, firstName, lastName, email);
    }

}
