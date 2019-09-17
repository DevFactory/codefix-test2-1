package com.devfactory.codefix.repositories.exception;

import static java.lang.String.format;

import java.net.URI;

public class NoRevisionFoundException extends RuntimeException {

    private static final long serialVersionUID = 4419416801598126431L;
    public static final String NO_REVISION_FOUND = "No revision found for the respository %s";

    public NoRevisionFoundException(URI uri) {
        super(format(NO_REVISION_FOUND, uri.toString()));
    }
}
