package com.sangdari.global.security.filter;

public final class SecurityUrlRegistry {

    // 생성자를 private으로 해서 인스턴스 생성을 방지함
    private SecurityUrlRegistry() {}

    // ----------------------
    // 블랙리스트 (인증 필요) : 반드시 로그인 상태여야 통과 가능
    // ----------------------
    public static final String[] AUTH_REQUIRED_GET_URLS = {
            "/api/vendors/{id}"
    };

    public static final String[] AUTH_REQUIRED_POST_URLS = {
            "/api/logout"
            , "/api/vendors"
    };

    public static final String[] AUTH_REQUIRED_PUT_URLS = {

    };

    public static final String[] AUTH_REQUIRED_PATCH_URLS = {

    };

    public static final String[] AUTH_REQUIRED_DELETE_URLS = {
            "/api/vendors/{id}"
    };

    // ----------------------
    // 화이트리스트 (인증 불필요)
    // ----------------------
    public static final String[] PUBLIC_GET_URLS = {

    };

    public static final String[] PUBLIC_POST_URLS = {

    };

    public static final String[] PUBLIC_PUT_URLS = {

    };

    public static final String[] PUBLIC_PATCH_URLS = {

    };

    public static final String[] PUBLIC_DELETE_URLS = {

    };
}
