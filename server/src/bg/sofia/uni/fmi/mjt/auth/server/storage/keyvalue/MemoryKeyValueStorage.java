package bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue;

import java.util.HashMap;
import java.util.Map;

public class MemoryKeyValueStorage<K, V> implements KeyValueDataStore<K, V> {

    private final Map<K, V> kvMap;

    public MemoryKeyValueStorage() {
        this.kvMap = new HashMap<>();
    }

    @Override
    public V put(final K key, final V value) {
        return kvMap.put(key, value);
    }

    @Override
    public V deleteByKey(final K key) {
        return kvMap.remove(key);
    }

    @Override
    public V getByKey(final K key) {
        return kvMap.get(key);
    }

    @Override
    public Map<K, V> getAll() {
        return kvMap;
    }

}
