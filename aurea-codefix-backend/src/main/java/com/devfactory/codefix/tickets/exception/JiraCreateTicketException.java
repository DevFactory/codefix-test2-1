package com.devfactory.codefix.tickets.exception;

public class JiraCreateTicketException extends RuntimeException {

    private static final long serialVersionUID = 45126667552706145L;

    public JiraCreateTicketException(String message) {
        super(message);
    }
}
