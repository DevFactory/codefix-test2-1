package com.devfactory.codefix.orderinformation.exception;

public class NotActiveOrderException extends RuntimeException {

    private static final long serialVersionUID = -3654080874092821860L;

    public NotActiveOrderException() {
        super("Not Active order found ");
    }
}
