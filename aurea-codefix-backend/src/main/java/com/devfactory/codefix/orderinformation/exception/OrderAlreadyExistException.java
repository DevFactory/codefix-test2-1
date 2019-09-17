package com.devfactory.codefix.orderinformation.exception;

public class OrderAlreadyExistException extends RuntimeException {

    private static final long serialVersionUID = -455410299015353831L;

    public OrderAlreadyExistException() {
        super("User already have an active order");
    }
}
