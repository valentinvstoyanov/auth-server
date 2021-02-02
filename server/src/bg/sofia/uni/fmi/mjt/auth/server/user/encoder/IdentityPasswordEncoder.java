package bg.sofia.uni.fmi.mjt.auth.server.user.encoder;

public class IdentityPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(final String rawPassword) {
        return rawPassword;
    }

    @Override
    public boolean match(final String rawPassword, final String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }

}
