package com.devfactory.codefix.repositories.exception;

public class RepoNoAccessException extends RuntimeException {

    private static final long serialVersionUID = -7187052911033850836L;

    public RepoNoAccessException(long repoId) {
        super("User is not allowed to activate repository id: " + repoId);
    }
}
