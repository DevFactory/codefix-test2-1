package com.devfactory.codefix.orderinformation.exception;

public class NotIssuesAvailableException extends RuntimeException {

    private static final long serialVersionUID = -9099978468894172489L;

    public NotIssuesAvailableException() {
        super("Not issues available were found to submit order");
    }
}
