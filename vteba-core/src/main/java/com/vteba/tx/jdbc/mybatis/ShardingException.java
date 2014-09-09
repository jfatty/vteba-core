package com.vteba.tx.jdbc.mybatis;


public class ShardingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ShardingException() {
        super();
    }

    public ShardingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ShardingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShardingException(String message) {
        super(message);
    }

    public ShardingException(Throwable cause) {
        super(cause);
    }

}
