package com.devfactory.codefix.security;

import lombok.Data;

/**
 * Auth0 security properties configuration.
 */
@Data
public class SecurityProperties {

    private String audience;
    private String issuer;
    private String domain;
    private String clientId;
    private String clientSecret;
}
