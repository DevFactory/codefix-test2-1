package com.devfactory.codefix.github.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GitReferenceRequest {

    private final String sha;
    private final boolean force;
}
