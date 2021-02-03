package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import java.io.IOException;
import java.util.Collection;

public interface AdminRepository {

    void createAdmin(String username) throws IOException;

    boolean isAdmin(String username);

    void deleteAdmin(String username) throws IOException;

    Collection<String> getAllAdmins();

}
