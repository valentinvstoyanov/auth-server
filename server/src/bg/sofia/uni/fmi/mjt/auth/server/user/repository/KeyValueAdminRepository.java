package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.KeyValueDataStore;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class KeyValueAdminRepository implements AdminRepository {

    private final KeyValueDataStore<String, String> adminStore;
    private final Set<String> adminCache;

    public KeyValueAdminRepository(final KeyValueDataStore<String, String> adminStore) throws IOException {
        this.adminStore = adminStore;
        this.adminCache = new HashSet<>();
        initAdminCache();
    }

    private void initAdminCache() throws IOException {
        adminCache.addAll(adminStore.getAll().keySet());
    }

    @Override
    public void createAdmin(final String username) throws IOException {
        adminStore.put(username, "");
        adminCache.add(username);
    }

    @Override
    public boolean isAdmin(final String username) {
        return adminCache.contains(username);
    }

    @Override
    public void deleteAdmin(final String username) throws IOException {
        adminCache.remove(username);
        adminStore.deleteByKey(username);
    }

    @Override
    public Collection<String> getAllAdmins() {
        return Collections.unmodifiableCollection(adminCache);
    }

}
