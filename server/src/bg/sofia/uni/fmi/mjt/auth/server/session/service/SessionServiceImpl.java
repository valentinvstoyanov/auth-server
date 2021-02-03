package bg.sofia.uni.fmi.mjt.auth.server.session.service;

import bg.sofia.uni.fmi.mjt.auth.server.session.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.session.repository.SessionRepository;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessionServiceImpl implements SessionService {

    private final Duration sessionDuration;
    private final SessionRepository sessionRepository;

    public SessionServiceImpl(final Duration sessionDuration, final SessionRepository sessionRepository) {
        this.sessionDuration = sessionDuration;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public String createSession(final String username) throws IOException {
        final LocalDateTime expiration = LocalDateTime.now().plus(sessionDuration);
        final Session session = new Session(UUID.randomUUID().toString(), expiration);
        sessionRepository.createSession(username, session);
        return session.id();
    }

    @Override
    public boolean deleteSessionById(final String sessionId) throws IOException {
        return sessionRepository.deleteSessionById(sessionId) != null;
    }

    /*TODO: Basically this will serve as a way to "authenticate"(sessionId), i.e
        authenticated <=> username != null
    */
    @Override
    public String getUsernameBySessionId(final String sessionId) throws IOException {
        return sessionRepository.getUsernameByIdSession(sessionId);
    }

    @Override
    public String updateSessionUsername(final String sessionId, final String username) throws IOException {
        final Session session = sessionRepository.deleteSessionById(sessionId);
        if (session == null) {
            return null;
        }
        sessionRepository.createSession(username, session);
        return session.id();
    }

}
