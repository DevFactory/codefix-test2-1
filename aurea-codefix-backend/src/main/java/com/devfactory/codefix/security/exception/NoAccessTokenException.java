package com.devfactory.codefix.security.exception;

public class NoAccessTokenException extends RuntimeException {

    private static final long serialVersionUID = -1253536924670973449L;

    public NoAccessTokenException() {
        super("Can not obtain access token");
    }
}
