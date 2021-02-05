package bg.sofia.uni.fmi.mjt.auth.server.authorization.repository;

import bg.sofia.uni.fmi.mjt.auth.server.authorization.model.Role;
import bg.sofia.uni.fmi.mjt.auth.server.storage.serializer.Serializer;

public class RoleSerializer implements Serializer<Role> {

    private final Serializer<String> stringSerializer;

    public RoleSerializer(final Serializer<String> stringSerializer) {
        this.stringSerializer = stringSerializer;
    }

    @Override
    public String serialize(final Role role) {
        return stringSerializer.serialize(role.name());
    }

    @Override
    public Role deserialize(final String str) {
        return new Role(stringSerializer.deserialize(str));
    }

}
