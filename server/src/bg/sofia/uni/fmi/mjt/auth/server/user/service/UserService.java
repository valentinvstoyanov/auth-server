package bg.sofia.uni.fmi.mjt.auth.server.user.service;

import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;

public interface UserService {

    /**
     * Registers the user. Then, the user is automatically logged in.
     * @param username unique string that specifies the username of the new user
     * @param password string which specifies the password of the new user
     * @param firstName string which specifies the first name of the new user
     * @param lastName string which specifies the last name of the new user
     * @param email string which specifies the email of the new user
     * @return the session id for the registered and logged user
     * @throws UsernameAlreadyTakenException If user with the same <code>username</code> is already registered
     * @throws InvalidUserDataException If the validation of <code>username</code>, <code>password</code>,
     *                                  <code>firstName</code>, <code>lastName</code> or <code>email</code> fails
     */
    String register(String username, String password, String firstName, String lastName, String email)
            throws UsernameAlreadyTakenException, InvalidUserDataException;

    /**
     * Logs the user in with the provided <code>username</code> and <code>password</code>.
     * If the user is already logged in, the old session is invalidated and new is created.
     * @param username string that specifies the username of the user
     * @param password string which specifies the password of the user
     * @return the session id for the logged user
     * @throws InvalidUserDataException If the validation of either <code>username</code> or
     *                                  <code>password</code> fails
     * @throws InvalidUsernamePasswordCombination If no user is registered user with <code>username</code> or cannot
     *                                        match <code>password</code> against the password of the registered user
     */
    String login(String username, String password) throws InvalidUserDataException, InvalidUsernamePasswordCombination;

    /**
     * Returns the <code>sessionId</code> if it is associated with a session.
     * @param sessionId string specifying the session to look for
     * @return <code>sessionId</code> if session with such id exists, null otherwise
     */
    String login(String sessionId);

    void logout(String sessionId);

    void update(String sessionId, String username, String password, String firstName, String lastName, String email);
    void changePassword(String sessionId, String username, String oldPassword, String newPassword);

    void addAdmin(String sessionId, String username);
    void removeAdmin(String sessionId, String username);
    void delete(String sessionId, String username);

}
