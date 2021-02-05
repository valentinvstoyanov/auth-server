package bg.sofia.uni.fmi.mjt.auth.server.storage.exception;

public class StorageException extends RuntimeException {

    public StorageException(final String message) {
        super(message);
    }

    public StorageException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public StorageException(final Throwable cause) {
        super(cause);
    }

}
