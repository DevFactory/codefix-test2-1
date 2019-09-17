package com.devfactory.codefix.tickets.exception;

public class JiraMoveTicketIssueStatusException extends RuntimeException {

    private static final long serialVersionUID = 45126667552706145L;

    public JiraMoveTicketIssueStatusException(String message, Exception error) {
        super("Error moving Jira ticket issue status: " + message, error);
    }

    public JiraMoveTicketIssueStatusException(String message) {
        super("Error moving Jira ticket issue status: " + message);
    }
}
