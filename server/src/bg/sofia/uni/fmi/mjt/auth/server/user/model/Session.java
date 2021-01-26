package bg.sofia.uni.fmi.mjt.auth.server.user.model;

import java.time.LocalDateTime;

public record Session(String id, LocalDateTime expirationDateTime) {

    public Session(final String id) {
        this(id, LocalDateTime.now());
    }

}
