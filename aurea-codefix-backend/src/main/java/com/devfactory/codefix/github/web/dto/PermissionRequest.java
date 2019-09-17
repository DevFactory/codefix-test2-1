package com.devfactory.codefix.github.web.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class PermissionRequest {

    private final String permission;

    public PermissionRequest(String permission) {
        this.permission = permission;
    }
}
