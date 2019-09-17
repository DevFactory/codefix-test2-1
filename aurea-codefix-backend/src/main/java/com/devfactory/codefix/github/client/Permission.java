package com.devfactory.codefix.github.client;

import com.devfactory.codefix.github.web.dto.PermissionRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Permission {

    WRITE(new PermissionRequest("push")),
    READONLY(new PermissionRequest("pull")),
    ADMIN(new PermissionRequest("admin"));

    private final PermissionRequest permissionRequest;

    public PermissionRequest asRequest() {
        return permissionRequest;
    }
}
