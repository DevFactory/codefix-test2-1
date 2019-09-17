package com.devfactory.codefix.codeserver;

import lombok.Data;

/**
 * Externals system related properties.
 */
@Data
public class CodeServerProperties {

    private String codeServerUrl;
    private boolean subscribeToCommitQueue;
}
