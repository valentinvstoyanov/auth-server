package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;

public interface UserRepository {

    User createUser(User user);

    User updateUser(String oldUsername, User newUser);

    User getUserByUsername(String username);

    User deleteUserByUsername(String username);

}
