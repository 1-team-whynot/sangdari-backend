package com.sangdari.global.security.filter;

public final class SecurityUrlRegistry {
    private SecurityUrlRegistry() {} // 인스턴스 생성 방지

    // ----------------------------
    // 블랙리스트 (인증 반드시 필요)
    // ----------------------------
    public static final String[] AUTH_REQUIRED_GET_URLS = {

    };
    public static final String[] AUTH_REQUIRED_POST_URLS = {
        "/api/auth/logout"
    };
    public static final String[] AUTH_REQUIRED_PUT_URLS = {

    };
    public static final String[] AUTH_REQUIRED_PATCH_URLS = {

    };
    public static final String[] AUTH_REQUIRED_DELETE_URLS = {

    };
}
