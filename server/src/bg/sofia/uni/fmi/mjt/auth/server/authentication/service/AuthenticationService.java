package bg.sofia.uni.fmi.mjt.auth.server.authentication.service;

public interface AuthenticationService {

    String authenticate(String username, String password);

    String refresh(String sessionId);

    boolean validate(String sessionId);

    boolean invalidate(String sessionId);

    String update(String sessionId, String username);

    String getUsernameBySessionId(String sessionId);

    String getSessionIdByUsername(String username);

}
