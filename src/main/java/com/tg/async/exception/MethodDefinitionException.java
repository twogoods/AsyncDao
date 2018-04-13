package com.tg.async.exception;

/**
 * Created by twogoods on 2018/4/12.
 */
public class MethodDefinitionException extends RuntimeException {
    public MethodDefinitionException() {
    }

    public MethodDefinitionException(String message) {
        super(message);
    }

    public MethodDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodDefinitionException(Throwable cause) {
        super(cause);
    }

    public MethodDefinitionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
