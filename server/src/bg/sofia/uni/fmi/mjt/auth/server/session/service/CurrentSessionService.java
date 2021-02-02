package bg.sofia.uni.fmi.mjt.auth.server.session.service;

public interface CurrentSessionService {

    void set(String sessionId);
    String get();

}
