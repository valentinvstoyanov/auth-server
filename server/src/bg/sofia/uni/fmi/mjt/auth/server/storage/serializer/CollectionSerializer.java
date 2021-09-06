package bg.sofia.uni.fmi.mjt.auth.server.storage.serializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CollectionSerializer<T> implements Serializer<Collection<T>> {

    private static final String ELEMENT_DELIMITER = ",";
    private static final String COLLECTION_PREFIX = "[";
    private static final String COLLECTION_SUFFIX = "]";

    private final Serializer<T> tSerializer;

    public CollectionSerializer(final Serializer<T> tSerializer) {
        this.tSerializer = tSerializer;
    }

    @Override
    public String serialize(final Collection<T> ts) {
        return ts.stream()
                .map(tSerializer::serialize)
                .collect(Collectors.joining(ELEMENT_DELIMITER, COLLECTION_PREFIX, COLLECTION_SUFFIX));
    }

    @Override
    public Collection<T> deserialize(final String str) {
        if (str.length() < COLLECTION_PREFIX.length() + COLLECTION_SUFFIX.length() ||
                !str.startsWith(COLLECTION_PREFIX) ||
                !str.endsWith(COLLECTION_SUFFIX)) {
            throw new IllegalArgumentException("Cannot deserialize invalid collection string: " + str);
        }

        final String elementsPart = str.substring(COLLECTION_PREFIX.length() + 1, str.length() - COLLECTION_SUFFIX.length());

        return Arrays.stream(elementsPart.split(ELEMENT_DELIMITER))
                .map(tSerializer::deserialize)
                .collect(Collectors.toList());
    }

}
