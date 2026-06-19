package com.sangdari.global.security.jwt;

import com.sangdari.domain.user.entities.User;
import com.sangdari.global.errors.custom.InvalidTokenException;
import com.sangdari.global.security.cookie.CookieManager;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtProvider {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final CookieManager cookieManager;

    public JwtProvider(JwtConfig jwtConfig, CookieManager cookieManager) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.secret()));
        this.cookieManager = cookieManager;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, jwtConfig.accessTokenExpiry());
    }

    // 리프레시 토큰 생성 (액세스 토큰 만료)
    public String generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.refreshTokenExpiry());
    }

    private String generateToken(User user, long ttl) {
        Date now = new Date();

        return Jwts.builder()
                .header()
                .type(jwtConfig.type())
                .and()
                .subject(String.valueOf(user.getUserId()))
                .issuer(jwtConfig.issuer())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttl))
                .signWith(secretKey)
                .compact();
    }

    // 쿠키에서 refreshToken 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return cookieManager.getCookie(request, jwtConfig.refreshTokenCookieName())
                .map(Cookie::getValue);
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(jwtConfig.headerKey());

        if (bearerToken == null || !bearerToken.startsWith(jwtConfig.scheme()))
            return Optional.empty();

        return Optional.of(bearerToken.substring(jwtConfig.scheme().length()).trim());
    }

    // 토큰 검증 및 Claim(Payload) 추출
    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token) // JWT signature 검증 및 Claim(Payload) 추출
                    .getPayload();
        } catch (ExpiredJwtException e) { // 토큰 만료 에러
            throw new InvalidTokenException("토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("토큰 서명이 위조되었습니다.");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("토큰 형식이 올바르지 않습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("토큰 검증에 실패했습니다.");
        }
    }
}
