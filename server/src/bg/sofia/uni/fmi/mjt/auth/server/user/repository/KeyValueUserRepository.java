package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.KeyValueDataStore;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;

import java.io.IOException;

public class KeyValueUserRepository implements UserRepository {

    private final KeyValueDataStore<String, User> userStore;
    private final KeyValueDataStore<String, User> userCache;

    public KeyValueUserRepository(final KeyValueDataStore<String, User> userStore,
                                  final KeyValueDataStore<String, User> userCache) throws IOException {

        this.userStore = userStore;
        this.userCache = userCache;
        initUserCache();
    }

    private void initUserCache() throws IOException {
        for (final var entry : userStore.getAll().entrySet()) {
            userCache.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void create(final User user) throws IOException {
        userStore.put(user.username(), user);
        userCache.put(user.username(), user);
    }

    @Override
    public void update(final String oldUsername, final User newUser) throws IOException {
        if (!oldUsername.equals(newUser.username())) {
            userStore.deleteByKey(oldUsername);
            userCache.deleteByKey(oldUsername);
        }

        userStore.put(newUser.username(), newUser);
        userCache.put(newUser.username(), newUser);
    }

    @Override
    public User getByUsername(final String username) throws IOException {
        return userCache.getByKey(username);
    }

    @Override
    public User deleteByUsername(final String username) throws IOException {
        final User user = userCache.deleteByKey(username);
        return user == null ? null : userStore.deleteByKey(username);
    }

}
