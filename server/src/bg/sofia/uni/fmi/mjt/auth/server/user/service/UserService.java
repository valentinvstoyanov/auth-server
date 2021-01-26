package bg.sofia.uni.fmi.mjt.auth.server.user.service;

import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;

public interface UserService {

    String register(String username, String password, String firstName, String lastName, String email)
            throws UsernameAlreadyTakenException, InvalidUserDataException;

    String login(String username, String password);
    String login(String sessionId);

    void logout(String sessionId);

    void update(String sessionId, String username, String password, String firstName, String lastName, String email);
    void changePassword(String sessionId, String username, String oldPassword, String newPassword);

    void addAdmin(String sessionId, String username);
    void removeAdmin(String sessionId, String username);
    void delete(String sessionId, String username);

}
