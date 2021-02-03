package bg.sofia.uni.fmi.mjt.auth.server.session.repository;

import bg.sofia.uni.fmi.mjt.auth.server.session.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.session.model.UsernameSession;
import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.KeyValueDataStore;

import java.io.IOException;

public class KeyValueSessionRepository implements SessionRepository {

    private final KeyValueDataStore<String, Session> usernameSessionStore;
    private final KeyValueDataStore<String, UsernameSession> sessionCache;

    public KeyValueSessionRepository(final KeyValueDataStore<String, Session> usernameSessionStore,
                                     final KeyValueDataStore<String, UsernameSession> sessionCache) throws IOException {

        this.usernameSessionStore = usernameSessionStore;
        this.sessionCache = sessionCache;
        initSessionCache();
    }

    private void initSessionCache() throws IOException {
        for (final var entry : usernameSessionStore.getAll().entrySet()) {
            sessionCache.put(entry.getValue().id(), new UsernameSession(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public void createSession(final String username, final Session session) throws IOException {
        usernameSessionStore.put(username, session);
        sessionCache.put(session.id(), new UsernameSession(username, session));
    }

    @Override
    public Session getSessionById(final String sessionId) throws IOException {
        return sessionCache.getByKey(sessionId).session();
    }

    @Override
    public Session deleteSessionById(final String sessionId) throws IOException {
        final UsernameSession usernameSession = sessionCache.deleteByKey(sessionId);
        return usernameSessionStore.deleteByKey(usernameSession.username());
    }

    @Override
    public String getUsernameByIdSession(final String sessionId) throws IOException {
        final UsernameSession usernameSession = sessionCache.getByKey(sessionId);
        return usernameSession == null ? null : usernameSession.username();
    }

}
