package bg.sofia.uni.fmi.mjt.auth.server.session.repository;

import bg.sofia.uni.fmi.mjt.auth.server.session.model.Session;

import java.io.IOException;

public interface SessionRepository {

    void createSession(String username, Session session) throws IOException;

    Session getSessionById(String sessionId) throws IOException;

    Session deleteSessionById(String sessionId) throws IOException;

    String getUsernameByIdSession(String sessionId) throws IOException;

}
