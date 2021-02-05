package bg.sofia.uni.fmi.mjt.auth.server.authentication.repository;

import bg.sofia.uni.fmi.mjt.auth.server.authentication.model.Session;

public interface AuthenticationRepository {

    Session createSession(String username, Session session);

    Session getSessionById(String sessionId);

    Session deleteSessionById(String sessionId);

    String getUsernameByIdSession(String sessionId);

    String getSessionIdByUsername(String username);

}
