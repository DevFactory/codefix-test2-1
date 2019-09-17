package com.devfactory.codefix.security.exception;

public class ManagementApiException extends RuntimeException {

    private static final long serialVersionUID = 4997422938150753068L;

    public ManagementApiException(Exception original) {
        super("Can not obtain auth0 management api", original);
    }

}
