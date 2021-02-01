package bg.sofia.uni.fmi.mjt.auth.server.storage;

public interface Serializer<T> {

    String serialize(T t);
    T deserialize(String str);

}
