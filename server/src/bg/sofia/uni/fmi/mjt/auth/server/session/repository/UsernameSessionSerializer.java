package bg.sofia.uni.fmi.mjt.auth.server.session.repository;

import bg.sofia.uni.fmi.mjt.auth.server.session.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.session.model.UsernameSession;
import bg.sofia.uni.fmi.mjt.auth.server.storage.serializer.Serializer;

public class UsernameSessionSerializer implements Serializer<UsernameSession> {

    private static final String FIELD_DELIM = " ";
    private static final int FIELD_COUNT = 2;
    private static final int USERNAME_INDEX = 0;
    private static final int SESSION_INDEX = 1;

    private final Serializer<Session> sessionSerializer;

    public UsernameSessionSerializer(final Serializer<Session> sessionSerializer) {
        this.sessionSerializer = sessionSerializer;
    }

    @Override
    public String serialize(final UsernameSession usernameSession) {
        return usernameSession.username() + FIELD_DELIM + sessionSerializer.serialize(usernameSession.session());
    }

    @Override
    public UsernameSession deserialize(final String str) {
        final String[] usernameWithSession = str.split(FIELD_DELIM, FIELD_COUNT);
        final String username = usernameWithSession[USERNAME_INDEX];
        final String sessionStr = usernameWithSession[SESSION_INDEX];
        return new UsernameSession(username, sessionSerializer.deserialize(sessionStr));
    }

}
