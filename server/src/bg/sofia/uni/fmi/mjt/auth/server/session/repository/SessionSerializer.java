package bg.sofia.uni.fmi.mjt.auth.server.session.repository;

import bg.sofia.uni.fmi.mjt.auth.server.session.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.storage.serializer.Serializer;

import java.time.LocalDateTime;

public class SessionSerializer implements Serializer<Session> {

    private static final String FIELD_DELIM = " ";
    private static final int FIELD_COUNT = 2;
    private static final int SESSION_ID_INDEX = 0;
    private static final int EXPIRATION_INDEX = 1;

    @Override
    public String serialize(final Session session) {
        return session.id() + FIELD_DELIM + session.expirationDateTime().toString();
    }

    @Override
    public Session deserialize(final String str) {
        final String[] sessionIdWithExpiration = str.split(FIELD_DELIM, FIELD_COUNT);
        final String sessionId = sessionIdWithExpiration[SESSION_ID_INDEX];
        final LocalDateTime expirationDateTime = LocalDateTime.parse(sessionIdWithExpiration[EXPIRATION_INDEX]);
        return new Session(sessionId, expirationDateTime);
    }

}
