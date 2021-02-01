package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.session.model.Session;

public interface SessionRepository {

    String createSession(String username);

    Session getSessionById(String sessionId);

    boolean deleteSessionById(String sessionId);

    String getUsernameByIdSession(String sessionId);

}
