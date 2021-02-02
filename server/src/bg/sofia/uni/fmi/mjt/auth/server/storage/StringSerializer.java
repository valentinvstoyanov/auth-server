package bg.sofia.uni.fmi.mjt.auth.server.storage;

public class StringSerializer implements Serializer<String> {

    @Override
    public String serialize(final String s) {
        return s;
    }

    @Override
    public String deserialize(final String str) {
        return str;
    }

}
