package bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue;

import java.util.Map;

public interface KeyValueDataStore<K, V> {

    V put(K key, V value);

    V deleteByKey(K key);

    V getByKey(K key);

    Map<K, V> getAll();

}
