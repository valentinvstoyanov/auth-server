package bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session;

public interface CurrentSessionIdService {

    void set(String username);

    void clear();

    String get();

}
