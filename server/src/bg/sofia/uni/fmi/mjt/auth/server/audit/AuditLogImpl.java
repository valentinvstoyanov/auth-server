package bg.sofia.uni.fmi.mjt.auth.server.audit;


import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.KeyValueDataStore;

import java.util.Collection;
import java.util.Collections;

public class AuditLogImpl implements AuditLog {

    private final KeyValueDataStore<String, Collection<String>> logStore;

    public AuditLogImpl(final KeyValueDataStore<String, Collection<String>> logStore) {
        this.logStore = logStore;
    }

    public void log(final String tag, final String message) {
        Collection<String> messages = logStore.getByKey(tag);
        if (messages == null) {
            messages = Collections.singleton(message);
        } else {
            messages.add(message);
        }
        logStore.put(tag, messages);
    }

    public Collection<String> getLogsByTag(final String tag) {
        return logStore.getByKey(tag);
    }

}
