package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;

import java.io.IOException;

public interface UserRepository {

    void create(User user) throws IOException;

    void update(String oldUsername, User newUser) throws IOException;

    User getByUsername(String username) throws IOException;

    User deleteByUsername(String username) throws IOException;

}
