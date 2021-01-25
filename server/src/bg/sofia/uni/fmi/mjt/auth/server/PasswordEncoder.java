package bg.sofia.uni.fmi.mjt.auth.server;

public interface PasswordEncoder {

    String encode(String rawPassword);

}
