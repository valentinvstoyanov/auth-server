package bg.sofia.uni.fmi.mjt.auth.server.user.service;

import bg.sofia.uni.fmi.mjt.auth.server.user.encoder.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.user.validator.UserValidator;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.SessionRepository;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.UserRepository;

import java.time.LocalDateTime;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(final UserRepository userRepository, final UserValidator userValidator,
                           final SessionRepository sessionRepository, final PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String register(final String username, final String password, final String firstName, final String lastName,
                           final String email) throws UsernameAlreadyTakenException, InvalidUserDataException {

        userValidator.validate(username, password, firstName, lastName, email);

        if (userRepository.getByUsername(username) != null) {
            throw new UsernameAlreadyTakenException(username);
        }

        final String encodedPassword = passwordEncoder.encode(password);
        final User user = new User(username, encodedPassword, firstName, lastName, email);
        userRepository.create(user);
        return sessionRepository.create(username);
    }

    @Override
    public String login(final String username, final String password)
            throws InvalidUserDataException, InvalidUsernamePasswordCombination {

        userValidator.validate(username, password);

        final User user = userRepository.getByUsername(username);
        if (user == null || !passwordEncoder.match(password, user.password())) {
            throw new InvalidUsernamePasswordCombination();
        }

        return sessionRepository.create(username);
    }

    @Override
    public String login(final String sessionId) {
        final Session session = sessionRepository.getById(sessionId);
        return session != null ? session.id() : null;
    }

    @Override
    public boolean logout(final String sessionId) {
        return sessionRepository.deleteById(sessionId);
    }

    @Override
    public void update(final String sessionId, final String username, final String password, final String firstName,
                       final String lastName, final String email)
            throws InvalidUserDataException, UsernameAlreadyTakenException {

        userValidator.validate(username, password, firstName, lastName, email);

        final String oldUsername = sessionRepository.getUsernameById(sessionId);
        if (!oldUsername.equals(username) && userRepository.getByUsername(username) != null) {
            throw new UsernameAlreadyTakenException(username);
        }

        final String encodedPassword = passwordEncoder.encode(password);
        final User user = new User(username, encodedPassword, firstName, lastName, email);
        userRepository.update(oldUsername, user);
    }

    @Override
    public void addAdmin(final String sessionId, final String username) {

    }

    @Override
    public void removeAdmin(final String sessionId, final String username) {

    }

    @Override
    public void delete(final String sessionId, final String username) {

    }

}
