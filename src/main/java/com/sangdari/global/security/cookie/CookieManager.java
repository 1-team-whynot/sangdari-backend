package com.sangdari.global.security.cookie;

import com.sangdari.global.security.jwt.JwtConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CookieManager {
    private final JwtConfig jwtConfig;

    public Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
            .filter(cookie -> cookie.getName().equals(cookieName))
            .findFirst();
    }

    public void setCookie(
        HttpServletResponse response
        , String name
        , String value
        , int maxAge
        , String path
    ) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtConfig.secure());

        response.addCookie(cookie);
    }
}
