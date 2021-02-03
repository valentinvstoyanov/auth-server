package bg.sofia.uni.fmi.mjt.auth.server.user.service;

import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.encoder.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.AdminRepository;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.auth.server.user.validator.UserValidator;

import java.io.IOException;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    public UserServiceImpl(final UserRepository userRepository,
                           final UserValidator userValidator,
                           final SessionService sessionService,
                           final PasswordEncoder passwordEncoder, final AdminRepository adminRepository) {

        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
    }

    @Override
    public String register(final String username,
                           final String password,
                           final String firstName,
                           final String lastName,
                           final String email)
            throws UsernameAlreadyTakenException, InvalidUserDataException, IOException {

        userValidator.validate(username, password, firstName, lastName, email);

        if (userRepository.getByUsername(username) != null) {
            throw new UsernameAlreadyTakenException(username);
        }

        final String encodedPassword = passwordEncoder.encode(password);
        final User user = new User(username, encodedPassword, firstName, lastName, email);
        userRepository.create(user);
        return sessionService.createSession(username);
    }

    @Override
    public String login(final String username, final String password)
            throws InvalidUserDataException, InvalidUsernamePasswordCombination, IOException {

        userValidator.validate(username, password);

        final User user = userRepository.getByUsername(username);
        if (user == null || !passwordEncoder.match(password, user.password())) {
            throw new InvalidUsernamePasswordCombination();
        }

        return sessionService.createSession(username);
    }

    @Override
    public String login(final String sessionId) throws IOException {
        final String username = sessionService.getUsernameBySessionId(sessionId);
        return username != null ? sessionId : null;
    }

    @Override
    public boolean logout(final String sessionId) throws IOException {
        return sessionService.deleteSessionById(sessionId);
    }

    @Override
    public void update(final String sessionId,
                       final String newUsername,
                       final String newFirstName,
                       final String newLastName,
                       final String newEmail,
                       final String oldPassword,
                       final String newPassword)
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException, InvalidUsernamePasswordCombination {

        final String oldUsername = sessionService.getUsernameBySessionId(sessionId);
        final User oldUser = userRepository.getByUsername(oldUsername);
        final boolean usernameChange = newUsername != null && !oldUsername.equals(newUsername);
        final boolean passwordChange = oldPassword != null && newPassword != null && !oldPassword.equals(newPassword);

        if (passwordChange && !passwordEncoder.match(oldPassword, oldUser.password())) {
            throw new InvalidUsernamePasswordCombination();
        }

        if (usernameChange && userRepository.getByUsername(newUsername) != null) {
            throw new UsernameAlreadyTakenException(newUsername);
        }

        final String username = usernameChange ? newUsername : oldUsername;
        final String firstName = newFirstName == null ? oldUser.firstName() : newFirstName;
        final String lastName = newLastName == null ? oldUser.lastName() : newLastName;
        final String email = newEmail == null ? oldUser.email() : newEmail;

        String encodedPassword;
        if (passwordChange) {
            userValidator.validate(username, newPassword, firstName, lastName, email);
            encodedPassword = passwordEncoder.encode(newPassword);
        } else {
            userValidator.validate(username, firstName, lastName, email);
            encodedPassword = oldUser.password();
        }

        final User newUser = new User(username, encodedPassword, firstName, lastName, email);
        userRepository.update(oldUsername, newUser);

        if (usernameChange) {
            sessionService.updateSessionUsername(sessionId, newUsername);
        }
    }

    @Override
    public User getByUsername(final String username) throws IOException {
        return userRepository.getByUsername(username);
    }

    @Override
    public boolean isAdmin(final String username) {
        return adminRepository.isAdmin(username);
    }

    @Override
    public void addAdmin(final String username) throws IOException {
        if (adminRepository.isAdmin(username)) {
            return;
        }
        adminRepository.createAdmin(username);
    }

    @Override
    public boolean removeAdmin(final String username) throws IOException {
        if (!adminRepository.isAdmin(username)) {
            return false;
        }
        if (adminRepository.getAllAdmins().size() == 1) {
            return false;
        }
        adminRepository.deleteAdmin(username);
        return true;
    }

    @Override
    public void delete(final String username) throws IOException {
        if (adminRepository.isAdmin(username) && !removeAdmin(username)) {
            return;
        }
        userRepository.deleteByUsername(username);
    }

}
