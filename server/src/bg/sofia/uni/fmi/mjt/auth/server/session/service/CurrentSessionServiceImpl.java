package bg.sofia.uni.fmi.mjt.auth.server.session.service;

import java.nio.channels.SelectionKey;
import java.util.function.Supplier;

public class CurrentSessionServiceImpl implements CurrentSessionService {

    private final Supplier<SelectionKey> selectionKeySupplier;

    public CurrentSessionServiceImpl(final Supplier<SelectionKey> selectionKeySupplier) {
        this.selectionKeySupplier = selectionKeySupplier;
    }

    @Override
    public void set(final String sessionId) {
        selectionKeySupplier.get().attach(sessionId);
    }

    @Override
    public String get() {
        return (String) selectionKeySupplier.get().attachment();
    }

}
