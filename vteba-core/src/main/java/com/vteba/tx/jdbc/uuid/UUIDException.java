package com.vteba.tx.jdbc.uuid;

public class UUIDException extends RuntimeException {

    private static final long serialVersionUID = 4663440855689761397L;

    public UUIDException() {
        super();
    }

    public UUIDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UUIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public UUIDException(String message) {
        super(message);
    }

    public UUIDException(Throwable cause) {
        super(cause);
    }

}
