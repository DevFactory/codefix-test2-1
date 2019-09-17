package com.devfactory.codefix.repositories.exception;

public class RepoNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4512024402352706145L;

    public RepoNotFoundException(long repoId) {
        super("Repository with id: " + repoId + " not found");
    }
}
