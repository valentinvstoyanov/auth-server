package bg.sofia.uni.fmi.mjt.auth.server.user.service;

import bg.sofia.uni.fmi.mjt.auth.server.session.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.session.service.SessionService;
import bg.sofia.uni.fmi.mjt.auth.server.user.encoder.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUsernamePasswordCombination;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.auth.server.user.validator.UserValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String TEST_SESSION_ID = "testSessionId";
    private static final Session TEST_SESSION = new Session(TEST_SESSION_ID, LocalDateTime.now());

    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_USERNAME1 = TEST_USERNAME + "1";
    private static final String TEST_PASSWORD = "testPassword";
    private static final String TEST_OLD_PASSWORD = "testOldPassword";
    private static final String TEST_FIRST_NAME = "testFirstName";
    private static final String TEST_LAST_NAME = "testLastName";
    private static final String TEST_EMAIL = "testEmail";
    private static final User TEST_USER =
            new User(TEST_USERNAME, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private SessionService sessionServiceMock;

    @Mock
    private UserValidator userValidatorMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserServiceImpl(userRepositoryMock,
                userValidatorMock,
                sessionServiceMock,
                passwordEncoderMock);
    }

    private void testRegisterWithInvalidData(final String username, final String password, final String firstName,
                                             final String lastName, final String email)
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException {

        doThrow(InvalidUserDataException.class)
                .when(userValidatorMock)
                .validate(username, password, firstName, lastName, email);

        userService.register(username, password, firstName, lastName, email);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRegisterThrowsWhenUsernameIsNotValid()
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException {

        testRegisterWithInvalidData("", TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRegisterThrowsWhenPasswordIsNotValid()
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException {

        testRegisterWithInvalidData(TEST_USERNAME, "", TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRegisterThrowsWhenFirstNameIsNotValid()
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException {

        testRegisterWithInvalidData(TEST_USERNAME, TEST_PASSWORD, "", TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRegisterThrowsWhenLastNameIsNotValid()
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException {

        testRegisterWithInvalidData(TEST_USERNAME, TEST_PASSWORD, TEST_FIRST_NAME, "", TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRegisterThrowsWhenEmailIsNotValid()
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException {

        testRegisterWithInvalidData(TEST_USERNAME, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, "");
    }

    @Test(expected = UsernameAlreadyTakenException.class)
    public void testRegisterThrowsWhenUserAlreadyExists()
            throws UsernameAlreadyTakenException, InvalidUserDataException, IOException {

        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);
        userService.register(TEST_USERNAME, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test
    public void testRegisterReturnsCorrectSessionId() throws UsernameAlreadyTakenException, InvalidUserDataException, IOException {
        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(null);
        when(sessionServiceMock.createSession(TEST_USERNAME)).thenReturn(TEST_SESSION_ID);
        when(passwordEncoderMock.encode(TEST_PASSWORD)).thenReturn(TEST_PASSWORD);

        final String actual =
                userService.register(TEST_USERNAME, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);

        assertEquals("should return test session id", TEST_SESSION_ID, actual);

        verify(sessionServiceMock, times(1)).createSession(TEST_USERNAME);
        verify(userRepositoryMock, times(1)).getByUsername(TEST_USERNAME);
        verify(userRepositoryMock, times(1)).create(TEST_USER);
    }

    private void testLoginWhenUsernamePasswordIsNotValid(final String username, final String password)
            throws InvalidUserDataException, InvalidUsernamePasswordCombination, IOException {

        doThrow(InvalidUserDataException.class)
                .when(userValidatorMock)
                .validate(username, password);

        userService.login(username, password);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testLoginThrowsWhenUsernameIsNotValid() throws InvalidUsernamePasswordCombination, InvalidUserDataException, IOException {
        testLoginWhenUsernamePasswordIsNotValid("", TEST_PASSWORD);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testLoginThrowsWhenPasswordIsNotValid()
            throws InvalidUsernamePasswordCombination, InvalidUserDataException, IOException {

        testLoginWhenUsernamePasswordIsNotValid(TEST_USERNAME, "");
    }

    @Test(expected = InvalidUsernamePasswordCombination.class)
    public void testLoginThrowsWhenUserIsNotRegistered()
            throws InvalidUsernamePasswordCombination, InvalidUserDataException, IOException {

        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(null);
        userService.login(TEST_USERNAME, TEST_PASSWORD);
    }

    @Test(expected = InvalidUsernamePasswordCombination.class)
    public void testLoginThrowsWhenPasswordsDoNotMatch()
            throws InvalidUsernamePasswordCombination, InvalidUserDataException, IOException {

        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);
        when(passwordEncoderMock.match(TEST_PASSWORD, TEST_PASSWORD)).thenReturn(false);
        userService.login(TEST_USERNAME, TEST_PASSWORD);
    }

    @Test
    public void testLoginReturnsCorrectSessionId()
            throws InvalidUsernamePasswordCombination, InvalidUserDataException, IOException {

        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);
        when(passwordEncoderMock.match(TEST_PASSWORD, TEST_PASSWORD)).thenReturn(true);
        when(sessionServiceMock.createSession(TEST_USERNAME)).thenReturn(TEST_SESSION_ID);

        final String actual = userService.login(TEST_USERNAME, TEST_PASSWORD);
        assertEquals("should return test session id", TEST_SESSION_ID, actual);

        verify(sessionServiceMock, times(1)).createSession(TEST_USERNAME);
        verify(userRepositoryMock, times(1)).getByUsername(TEST_USERNAME);
    }

    @Test
    public void testLoginWithSessionIdReturnsNullWhenThereIsNoSuchSession() throws IOException {
        when(sessionServiceMock.getUsernameBySessionId(TEST_SESSION_ID)).thenReturn(null);

        final String actual = userService.login(TEST_SESSION_ID);
        assertNull("should return null", actual);
    }

    @Test
    public void testLoginWithSessionIdReturnsSessionIdWhenThereIsSuchSession() throws IOException {
        when(sessionServiceMock.getUsernameBySessionId(TEST_SESSION_ID)).thenReturn(TEST_USERNAME);

        final String actual = userService.login(TEST_SESSION_ID);
        assertEquals("should return test session id", TEST_SESSION_ID, actual);
    }

    private void testUpdateWithInvalidData(final String sessionId, final String username, final String oldPassword,
                                           final String newPassword, final String firstName, final String lastName,
                                           final String email)
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException, InvalidUsernamePasswordCombination {

        if (oldPassword == null) {
            doThrow(InvalidUserDataException.class)
                    .when(userValidatorMock)
                    .validate(username, firstName, lastName, email);
        } else {
            doThrow(InvalidUserDataException.class)
                    .when(userValidatorMock)
                    .validate(username, newPassword, firstName, lastName, email);
        }

        userService.update(sessionId, username, firstName, lastName, email, oldPassword, newPassword);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testUpdateUserDataThrowsWhenUsernameIsNotValid()
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException, InvalidUsernamePasswordCombination {

        final String invalidUsername = "";
        when(sessionServiceMock.getUsernameBySessionId(TEST_SESSION_ID)).thenReturn(TEST_USERNAME);
        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);
        testUpdateWithInvalidData(TEST_SESSION_ID, invalidUsername,
                null, null, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testUpdateUserDataThrowsWhenPasswordIsNotValid()
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException, InvalidUsernamePasswordCombination {

        when(sessionServiceMock.getUsernameBySessionId(TEST_SESSION_ID)).thenReturn(TEST_USERNAME);
        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);
        testUpdateWithInvalidData(TEST_SESSION_ID, TEST_USERNAME, TEST_PASSWORD, "", TEST_FIRST_NAME,
                TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testUpdateUserDataThrowsWhenFirstNameIsNotValid()
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException, InvalidUsernamePasswordCombination {

        when(sessionServiceMock.getUsernameBySessionId(TEST_SESSION_ID)).thenReturn(TEST_USERNAME);
        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);
        testUpdateWithInvalidData(TEST_SESSION_ID, TEST_USERNAME, null, null, "",
                TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testUpdateUserDataThrowsWhenLastNameIsNotValid()
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException, InvalidUsernamePasswordCombination {

        when(sessionServiceMock.getUsernameBySessionId(TEST_SESSION_ID)).thenReturn(TEST_USERNAME);
        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);
        testUpdateWithInvalidData(TEST_SESSION_ID, TEST_USERNAME, null, null, TEST_FIRST_NAME,
                "", TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testUpdateUserDataThrowsWhenEmailIsNotValid()
            throws InvalidUserDataException, UsernameAlreadyTakenException, IOException, InvalidUsernamePasswordCombination {

        when(sessionServiceMock.getUsernameBySessionId(TEST_SESSION_ID)).thenReturn(TEST_USERNAME);
        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);
        testUpdateWithInvalidData(TEST_SESSION_ID, TEST_USERNAME, null, null, TEST_FIRST_NAME,
                TEST_LAST_NAME, "");
    }

    @Test(expected = UsernameAlreadyTakenException.class)
    public void testUpdateThrowsWhenUsernameAlreadyTaken()
            throws UsernameAlreadyTakenException, InvalidUserDataException, IOException, InvalidUsernamePasswordCombination {

        when(sessionServiceMock.getUsernameBySessionId(TEST_SESSION_ID)).thenReturn(TEST_USERNAME);
        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);
        when(userRepositoryMock.getByUsername(TEST_USERNAME1)).thenReturn(TEST_USER);
        userService.update(TEST_SESSION_ID, TEST_USERNAME1, null, null, TEST_FIRST_NAME,
                TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test
    public void testUpdateWithoutUsernameChange() throws InvalidUserDataException, UsernameAlreadyTakenException, IOException, InvalidUsernamePasswordCombination {
        when(sessionServiceMock.getUsernameBySessionId(TEST_SESSION_ID)).thenReturn(TEST_USERNAME);
        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);

        userService.update(TEST_SESSION_ID, TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, null, null);
        verify(userRepositoryMock, times(1)).update(TEST_USERNAME, TEST_USER);
    }

    @Test
    public void testUpdateWithUsernameChange() throws InvalidUserDataException, UsernameAlreadyTakenException, IOException, InvalidUsernamePasswordCombination {
        final String oldUsername = TEST_USERNAME;
        final String newUsername = oldUsername + "1";
        final var newUser = new User(newUsername, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);

        when(sessionServiceMock.getUsernameBySessionId(TEST_SESSION_ID)).thenReturn(oldUsername);
        when(userRepositoryMock.getByUsername(oldUsername)).thenReturn(TEST_USER);
        when(userRepositoryMock.getByUsername(newUsername)).thenReturn(null);

        userService.update(TEST_SESSION_ID, newUsername, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, null, null);
        verify(userRepositoryMock, times(1)).update(oldUsername, newUser);
    }

}