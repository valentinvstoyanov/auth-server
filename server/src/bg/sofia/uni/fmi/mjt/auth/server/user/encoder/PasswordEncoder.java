package bg.sofia.uni.fmi.mjt.auth.server.user.encoder;

public interface PasswordEncoder {

    String encode(String rawPassword);

    boolean match(String rawPassword, String encodedPassword);

}
