package bg.sofia.uni.fmi.mjt.auth.server.authentication.service.session;

import java.nio.channels.SelectionKey;
import java.util.function.Supplier;

public class CurrentSessionIdServiceImpl implements CurrentSessionIdService {

    private final Supplier<SelectionKey> selectionKeySupplier;

    public CurrentSessionIdServiceImpl(final Supplier<SelectionKey> selectionKeySupplier) {
        this.selectionKeySupplier = selectionKeySupplier;
    }

    @Override
    public void set(final String username) {
        selectionKeySupplier.get().attach(username);
    }

    @Override
    public String get() {
        final Object attachment = selectionKeySupplier.get().attachment();
        return attachment == null ? null : (String) attachment;
    }

    @Override
    public void clear() {
        selectionKeySupplier.get().attach(null);
    }

}
