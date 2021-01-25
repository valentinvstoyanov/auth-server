package bg.sofia.uni.fmi.mjt.auth.server;

public record User(String username, String password, String firstName, String lastName, String email) {
}
