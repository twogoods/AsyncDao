package com.tg.async.exception;

/**
 * Created by twogoods on 2018/4/13.
 */
public class BuilderException extends RuntimeException{
    public BuilderException() {
    }

    public BuilderException(String message) {
        super(message);
    }

    public BuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public BuilderException(Throwable cause) {
        super(cause);
    }
}
