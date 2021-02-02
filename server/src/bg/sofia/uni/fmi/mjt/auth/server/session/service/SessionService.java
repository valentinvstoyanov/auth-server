package bg.sofia.uni.fmi.mjt.auth.server.session.service;

import java.io.IOException;

public interface SessionService {

    String createSession(String username) throws IOException;

    boolean deleteSessionById(String sessionId) throws IOException;

    String getUsernameBySessionId(String sessionId) throws IOException;

    String updateSessionUsername(String sessionId, String username) throws IOException;

}
