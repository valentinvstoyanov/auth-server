package bg.sofia.uni.fmi.mjt.auth.server.user.service;

import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;

public interface UserService {

    User createUser(String username,
                    String password,
                    String firstName,
                    String lastName,
                    String email)
            throws UsernameAlreadyTakenException, InvalidUserDataException;

    User updateUser(String currentUsername,
                    String newUsername,
                    String newFirstName,
                    String newLastName,
                    String newEmail,
                    String currentPassword,
                    String newPassword)
            throws InvalidUserDataException, UsernameAlreadyTakenException, InvalidUsernamePasswordCombination;

    User getUserByUsername(String username);

    User deleteUserByUsername(String username);

}
