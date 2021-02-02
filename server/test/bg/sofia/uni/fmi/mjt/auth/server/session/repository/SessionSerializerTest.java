package bg.sofia.uni.fmi.mjt.auth.server.session.repository;

import bg.sofia.uni.fmi.mjt.auth.server.session.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.storage.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

public class SessionSerializerTest {

    private static final String TEST_SESSION_ID = "testSessionId";
    private static final LocalDateTime TEST_EXPIRATION = LocalDateTime.of(2021, 5, 4, 1, 2, 3);
    private static final Session TEST_SESSION = new Session(TEST_SESSION_ID, TEST_EXPIRATION);

    private final Serializer<Session> sessionSerializer = new SessionSerializer();

    @Test
    public void testDeserializingSerializedSessionReturnsTheSameSession() {
        final String serialized = sessionSerializer.serialize(TEST_SESSION);
        final Session actual = sessionSerializer.deserialize(serialized);
        Assert.assertEquals("should be same", TEST_SESSION, actual);
    }

    @Test
    public void testDeserializingSerializedSessionReturnsTheSameSession1() {
        final String testSerialized = sessionSerializer.serialize(TEST_SESSION);

        final Session deserialized = sessionSerializer.deserialize(testSerialized);
        final String actual = sessionSerializer.serialize(deserialized);
        Assert.assertEquals("should be same", testSerialized, actual);
    }
}