package com.tzesh.springtemplate.enumerator.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Permission is an enum for permissions
 * @author tzesh
 */
@RequiredArgsConstructor
public enum PermissionEnum {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    MANAGER_READ("management:read"),
    MANAGER_UPDATE("management:update"),
    MANAGER_CREATE("management:create"),
    MANAGER_DELETE("management:delete");

    @Getter
    private final String permission;
}
