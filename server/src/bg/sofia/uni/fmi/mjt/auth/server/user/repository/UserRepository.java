package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;

public interface UserRepository {

    void create(User user);

    void update(String oldUsername, User newUser);

    User getByUsername(String username);

    void createAdmin(String username);

    void deleteAdmin(String username);

    void delete(String username);

}
