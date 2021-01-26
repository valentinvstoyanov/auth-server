package bg.sofia.uni.fmi.mjt.auth.server.user.repository;

import bg.sofia.uni.fmi.mjt.auth.server.user.model.Session;

public interface SessionRepository {

    String create(String username);

    Session getById(String sessionId);

}
