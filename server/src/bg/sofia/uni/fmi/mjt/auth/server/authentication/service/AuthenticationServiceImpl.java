package bg.sofia.uni.fmi.mjt.auth.server.authentication.service;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.repository.AuthenticationRepository;
import bg.sofia.uni.fmi.mjt.auth.server.user.encoder.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import bg.sofia.uni.fmi.mjt.auth.server.user.service.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuthenticationServiceImpl implements AuthenticationService {

    private final Duration sessionDuration;
    private final AuthenticationRepository authenticationRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(final Duration sessionDuration,
                                     final AuthenticationRepository authenticationRepository,
                                     final UserService userService,
                                     final PasswordEncoder passwordEncoder) {
        this.sessionDuration = sessionDuration;
        this.authenticationRepository = authenticationRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    private Session generateSession() {
        final String sessionId = UUID.randomUUID().toString();
        final LocalDateTime expirationDateTime = LocalDateTime.now().plus(sessionDuration);
        return new Session(sessionId, expirationDateTime);
    }

    @Override
    public String authenticate(final String username, final String password) {
        final User user = userService.getUserByUsername(username);
        if (user == null || !passwordEncoder.match(password, user.password())) {
            return null;
        }

        final Session session = generateSession();
        return authenticationRepository.createSession(username, session).id();
    }

    @Override
    public String refresh(final String sessionId) {
        final String username = authenticationRepository.getUsernameByIdSession(sessionId);
        if (username == null) {
            return null;
        }
        final Session session = generateSession();
        return authenticationRepository.createSession(username, session).id();
    }

    @Override
    public boolean validate(final String sessionId) {
        return authenticationRepository.getUsernameByIdSession(sessionId) != null;
    }

    @Override
    public boolean invalidate(final String sessionId) {
        return authenticationRepository.deleteSessionById(sessionId) != null;
    }

    @Override
    public String update(final String sessionId, final String username) {
        final Session session = authenticationRepository.deleteSessionById(sessionId);
        if (session == null) {
            return null;
        }
        return authenticationRepository.createSession(username, session).id();
    }

    @Override
    public String getUsernameBySessionId(final String sessionId) {
        return authenticationRepository.getUsernameByIdSession(sessionId);
    }

    @Override
    public String getSessionIdByUsername(final String username) {
        return authenticationRepository.getSessionIdByUsername(username);
    }

}
