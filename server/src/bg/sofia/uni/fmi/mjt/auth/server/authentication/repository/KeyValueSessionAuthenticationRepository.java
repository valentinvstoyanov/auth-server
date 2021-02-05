package bg.sofia.uni.fmi.mjt.auth.server.authentication.repository;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.authentication.model.UsernameSession;
import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.KeyValueDataStore;

public class KeyValueSessionAuthenticationRepository implements AuthenticationRepository {

    private final KeyValueDataStore<String, Session> usernameSessionStore;
    private final KeyValueDataStore<String, UsernameSession> sessionCache;

    public KeyValueSessionAuthenticationRepository(final KeyValueDataStore<String, Session> usernameSessionStore,
                                                   final KeyValueDataStore<String, UsernameSession> sessionCache) {
        this.usernameSessionStore = usernameSessionStore;
        this.sessionCache = sessionCache;
        initSessionCache();
    }

    private void initSessionCache() {
        for (final var entry : usernameSessionStore.getAll().entrySet()) {
            sessionCache.put(entry.getValue().id(), new UsernameSession(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public Session createSession(final String username, final Session session) {
        usernameSessionStore.put(username, session);
        sessionCache.put(session.id(), new UsernameSession(username, session));
        return session;
    }

    @Override
    public Session getSessionById(final String sessionId) {
        return sessionCache.getByKey(sessionId).session();
    }

    @Override
    public Session deleteSessionById(final String sessionId) {
        final UsernameSession usernameSession = sessionCache.deleteByKey(sessionId);
        return usernameSessionStore.deleteByKey(usernameSession.username());
    }

    @Override
    public String getUsernameByIdSession(final String sessionId) {
        final UsernameSession usernameSession = sessionCache.getByKey(sessionId);
        System.out.println(sessionId);
        System.out.println(usernameSession);
        return usernameSession == null ? null : usernameSession.username();
    }

    @Override
    public String getSessionIdByUsername(final String username) {
        final Session session = usernameSessionStore.getByKey(username);
        return session == null ? null : session.id();
    }

}
