package bg.sofia.uni.fmi.mjt.auth.server.storage;

import java.io.IOException;
import java.util.Map;

public interface KeyValueDataStore<K, V> {

    V put(K key, V value) throws IOException;

    V deleteByKey(K key) throws IOException;

    V getByKey(K key) throws IOException;

    Map<K, V> getAll() throws IOException;

}
