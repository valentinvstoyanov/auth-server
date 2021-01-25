package bg.sofia.uni.fmi.mjt.auth.server;

public interface UserRepository {

    void create(User user);

    User getByUsername(String username);

}
