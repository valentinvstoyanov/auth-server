package bg.sofia.uni.fmi.mjt.auth.server.audit;

import java.util.Collection;

public interface AuditLog {

    void log(final String tag, final String message);

    Collection<String> getLogsByTag(final String tag);

}
