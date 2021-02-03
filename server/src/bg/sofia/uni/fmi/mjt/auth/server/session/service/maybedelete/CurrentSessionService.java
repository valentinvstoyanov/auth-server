package bg.sofia.uni.fmi.mjt.auth.server.session.service.maybedelete;

public interface CurrentSessionService {

    void set(String sessionId);
    String get();

}
