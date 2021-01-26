package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;

public interface UserRepository {

    void create(User user);

    User getByUsername(String username);

}
