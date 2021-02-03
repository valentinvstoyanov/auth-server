package bg.sofia.uni.fmi.mjt.auth.server.authorization.service;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue.KeyValueDataStore;

import java.io.IOException;

public class KeyValueAuthorizationService implements AuthorizationService {

    private final KeyValueDataStore<String, Role> roleStore;

    public KeyValueAuthorizationService(final KeyValueDataStore<String, Role> roleStore) {
        this.roleStore = roleStore;
    }

    @Override
    public void setRole(final String username, final Role role) {
        try {
            roleStore.put(username, role);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            //TODO
        }
    }

    @Override
    public boolean authorize(final String username, final Role role) {
        try {
            return roleStore.getByKey(username).equals(role);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            //TODO
            return false;
        }
    }

}
