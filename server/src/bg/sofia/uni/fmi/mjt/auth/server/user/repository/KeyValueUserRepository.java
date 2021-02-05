package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.KeyValueDataStore;
import bg.sofia.uni.fmi.mjt.auth.server.user.model.User;

public class KeyValueUserRepository implements UserRepository {

    private final KeyValueDataStore<String, User> userStore;
    private final KeyValueDataStore<String, User> userCache;

    public KeyValueUserRepository(final KeyValueDataStore<String, User> userStore,
                                  final KeyValueDataStore<String, User> userCache) {

        this.userStore = userStore;
        this.userCache = userCache;
        initUserCache();
    }

    private void initUserCache() {
        for (final var entry : userStore.getAll().entrySet()) {
            userCache.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public User createUser(final User user) {
        userStore.put(user.username(), user);
        userCache.put(user.username(), user);
        return user;
    }

    @Override
    public User updateUser(final String oldUsername, final User newUser) {
        if (!oldUsername.equals(newUser.username())) {
            userStore.deleteByKey(oldUsername);
            userCache.deleteByKey(oldUsername);
        }

        userStore.put(newUser.username(), newUser);
        userCache.put(newUser.username(), newUser);
        return newUser;
    }

    @Override
    public User getUserByUsername(final String username) {
        return userCache.getByKey(username);
    }

    @Override
    public User deleteUserByUsername(final String username) {
        final User user = userCache.deleteByKey(username);
        return user == null ? null : userStore.deleteByKey(username);
    }

}
