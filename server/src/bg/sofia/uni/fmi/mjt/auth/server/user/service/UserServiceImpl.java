package bg.sofia.uni.fmi.mjt.auth.server.user.service;

import bg.sofia.uni.fmi.mjt.auth.server.user.encoder.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.auth.server.user.validator.UserValidator;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(final UserRepository userRepository,
                           final UserValidator userValidator,
                           final PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(final String username,
                           final String password,
                           final String firstName,
                           final String lastName,
                           final String email)
            throws UsernameAlreadyTakenException, InvalidUserDataException {

        userValidator.validate(username, password, firstName, lastName, email);
        takenUsernameCheck(username);

        final String encodedPassword = passwordEncoder.encode(password);
        final User user = new User(username, encodedPassword, firstName, lastName, email);
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(final String currentUsername,
                           final String newUsername,
                           final String newFirstName,
                           final String newLastName,
                           final String newEmail,
                           final String currentPassword,
                           final String newPassword)
            throws InvalidUserDataException, UsernameAlreadyTakenException, InvalidUsernamePasswordCombination {

        final User currentUser = userRepository.getUserByUsername(currentUsername);
        final boolean usernameChanged = newUsername != null && !currentUsername.equals(newUsername);
        final boolean passwordChanged = currentPassword != null && newPassword != null &&
                !currentPassword.equals(newPassword);

        if (usernameChanged) {
            takenUsernameCheck(newUsername);
        }

        final String username = usernameChanged ? newUsername : currentUsername;
        final String firstName = newFirstName == null ? currentUser.firstName() : newFirstName;
        final String lastName = newLastName == null ? currentUser.lastName() : newLastName;
        final String email = newEmail == null ? currentUser.email() : newEmail;

        String encodedPassword;
        if (passwordChanged) {
            passwordsMatchCheck(currentPassword, currentUser.password());
            userValidator.validate(username, newPassword, firstName, lastName, email);
            encodedPassword = passwordEncoder.encode(newPassword);
        } else {
            userValidator.validate(username, firstName, lastName, email);
            encodedPassword = currentUser.password();
        }

        final User newUser = new User(username, encodedPassword, firstName, lastName, email);
        return userRepository.updateUser(currentUsername, newUser);
    }

    @Override
    public User getUserByUsername(final String username) {
        return userRepository.getUserByUsername(username);
    }

    @Override
    public User deleteUserByUsername(final String username) {
        return userRepository.deleteUserByUsername(username);
    }

    private void passwordsMatchCheck(final String raw, final String encoded) throws InvalidUsernamePasswordCombination {
        if (!passwordEncoder.match(raw, encoded)) {
            throw new InvalidUsernamePasswordCombination();
        }
    }

    private void takenUsernameCheck(final String username) throws UsernameAlreadyTakenException {
        final User user = userRepository.getUserByUsername(username);
        if (user != null) {
            throw new UsernameAlreadyTakenException(username);
        }
    }

}
