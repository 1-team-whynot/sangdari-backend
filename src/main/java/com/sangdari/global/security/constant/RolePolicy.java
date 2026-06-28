package com.sangdari.global.security.constant;

import lombok.Getter;

@Getter
public enum RolePolicy {
    USER("USER")
    , OWNER("OWNER")
    , SUPER("SUPER");

    private final String role;

    RolePolicy(String role) {
        this.role = role;
    }
}
