package bg.sofia.uni.fmi.mjt.auth.server.authentication.model;

import java.time.LocalDateTime;

public record Session(String id, LocalDateTime expirationDateTime) {

}
