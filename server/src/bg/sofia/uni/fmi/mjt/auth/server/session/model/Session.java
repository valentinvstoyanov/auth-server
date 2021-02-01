package bg.sofia.uni.fmi.mjt.auth.server.session.model;

import java.time.LocalDateTime;

public record Session(String id, LocalDateTime expirationDateTime) {

}
