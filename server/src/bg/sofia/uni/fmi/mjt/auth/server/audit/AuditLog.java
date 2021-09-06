package bg.sofia.uni.fmi.mjt.auth.server.audit;

public interface AuditLog {

    void log(final String tag, final String message);

    String getLogsByTag(final String tag);

}
