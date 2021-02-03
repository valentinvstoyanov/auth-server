package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.storage.serializer.Serializer;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;
import org.junit.Assert;
import org.junit.Test;

public class UserSerializerTest {

    private static final User TEST_USER = new User("testUsername",
            "testPassword",
            "testFirstName",
            "testLastName",
            "testEmail");

    private final Serializer<User> userSerializer = new UserSerializer();

    @Test
    public void testDeserializingSerializedUserReturnsTheSameUser() {
        final String serialized = userSerializer.serialize(TEST_USER);
        final User actual = userSerializer.deserialize(serialized);
        Assert.assertEquals("should be the same user", TEST_USER, actual);
    }

    @Test
    public void testDeserializingSerializedUserReturnsTheSameUser1() {
        final String testSerialized = userSerializer.serialize(TEST_USER);

        final User deserialized = userSerializer.deserialize(testSerialized);
        final String actual = userSerializer.serialize(deserialized);
        Assert.assertEquals("should be same", testSerialized, actual);
    }

}