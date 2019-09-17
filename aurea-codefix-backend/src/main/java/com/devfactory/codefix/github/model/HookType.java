package com.devfactory.codefix.github.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HookType {
    PULL_REQUEST("pull_request");

    private final String value;
}
