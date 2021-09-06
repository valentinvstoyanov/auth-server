package bg.sofia.uni.fmi.mjt.auth.server.audit;


import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.KeyValueDataStore;

public class AuditLogImpl implements AuditLog {

    private static final String MESSAGE_DELIMITER = ", ";

    private final KeyValueDataStore<String, String> logStore;

    public AuditLogImpl(final KeyValueDataStore<String, String> logStore) {
        this.logStore = logStore;
    }

    public void log(final String tag, final String message) {
        String messages = logStore.getByKey(tag);
        if (messages == null) {
            messages = message + MESSAGE_DELIMITER;
        } else {
            messages += message + ", ";
        }
        logStore.put(tag, messages);
    }

    public String getLogsByTag(final String tag) {
        return logStore.getByKey(tag);
    }

}
