package bg.sofia.uni.fmi.mjt.auth.server.session.repository;

import bg.sofia.uni.fmi.mjt.auth.server.session.model.Session;
import bg.sofia.uni.fmi.mjt.auth.server.storage.Serializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SessionSerializer implements Serializer<Session> {

    private static final String FIELD_DELIM = " ";
    private static final int FIELD_COUNT = 2;
    private static final int SESSION_ID_INDEX = 0;
    private static final int EXPIRATION_INDEX = 1;

    @Override
    public String serialize(final Session session) {
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(session.expirationDateTime(), ZoneId.systemDefault());
        return session.id() + FIELD_DELIM + zonedDateTime.toInstant().toEpochMilli();
    }

    @Override
    public Session deserialize(final String str) {
        final String[] sessionIdWithExpiration = str.split(FIELD_DELIM, FIELD_COUNT);
        final String sessionId = sessionIdWithExpiration[SESSION_ID_INDEX];
        final long expirationMillis = Long.parseLong(sessionIdWithExpiration[EXPIRATION_INDEX]);
        final LocalDateTime expirationDateTime = Instant.ofEpochMilli(expirationMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return new Session(sessionId, expirationDateTime);
    }

}
