package bg.sofia.uni.fmi.mjt.auth.server.user.service;

import bg.sofia.uni.fmi.mjt.auth.server.user.encoder.PasswordEncoder;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.InvalidUserDataException;
import bg.sofia.uni.fmi.mjt.auth.server.user.exception.UsernameAlreadyTakenException;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.SessionRepository;
import bg.sofia.uni.fmi.mjt.auth.server.user.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.auth.server.user.validator.UserValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final String TEST_SESSION_ID = "testSessionId";
    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_PASSWORD = "testPassword";
    private static final String TEST_FIRST_NAME = "testFirstName";
    private static final String TEST_LAST_NAME = "testLastName";
    private static final String TEST_EMAIL = "testEmail";
    private static final User TEST_USER =
            new User(TEST_USERNAME, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private SessionRepository sessionRepositoryMock;

    @Mock
    private UserValidator userValidatorMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserServiceImpl(userRepositoryMock, userValidatorMock, sessionRepositoryMock,
                passwordEncoderMock);
    }

    private void testRegisterWithInvalidData(final String username, final String password, final String firstName,
                                             final String lastName, final String email)
            throws InvalidUserDataException, UsernameAlreadyTakenException {

        doThrow(InvalidUserDataException.class)
                .when(userValidatorMock)
                .validate(username, password, firstName, lastName, email);

        userService.register(username, password, firstName, lastName, email);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRegisterThrowsOnInvalidUsername() throws InvalidUserDataException, UsernameAlreadyTakenException {
        testRegisterWithInvalidData("", TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRegisterThrowsOnInvalidPassword() throws InvalidUserDataException, UsernameAlreadyTakenException {
        testRegisterWithInvalidData(TEST_USERNAME, "", TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRegisterThrowsOnInvalidFirstName() throws InvalidUserDataException, UsernameAlreadyTakenException {
        testRegisterWithInvalidData(TEST_USERNAME, TEST_PASSWORD, "", TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRegisterThrowsOnInvalidLastName() throws InvalidUserDataException, UsernameAlreadyTakenException {
        testRegisterWithInvalidData(TEST_USERNAME, TEST_PASSWORD, TEST_FIRST_NAME, "", TEST_EMAIL);
    }

    @Test(expected = InvalidUserDataException.class)
    public void testRegisterThrowsOnInvalidEmail() throws InvalidUserDataException, UsernameAlreadyTakenException {
        testRegisterWithInvalidData(TEST_USERNAME, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, "");
    }

    @Test(expected = UsernameAlreadyTakenException.class)
    public void testRegisterThrowsOnExistingUser() throws UsernameAlreadyTakenException, InvalidUserDataException {
        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(TEST_USER);
        userService.register(TEST_USERNAME, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);
    }

    @Test
    public void testRegisterReturnsSessionId() throws UsernameAlreadyTakenException, InvalidUserDataException {
        when(userRepositoryMock.getByUsername(TEST_USERNAME)).thenReturn(null);
        when(sessionRepositoryMock.create(TEST_USERNAME)).thenReturn(TEST_SESSION_ID);
        when(passwordEncoderMock.encode(TEST_PASSWORD)).thenReturn(TEST_PASSWORD);

        final String actual =
                userService.register(TEST_USERNAME, TEST_PASSWORD, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL);

        assertEquals("should return test session id", TEST_SESSION_ID, actual);

        verify(sessionRepositoryMock, times(1)).create(TEST_USERNAME);
        verify(userRepositoryMock, times(1)).getByUsername(TEST_USERNAME);
        verify(userRepositoryMock, times(1)).create(TEST_USER);
    }

}