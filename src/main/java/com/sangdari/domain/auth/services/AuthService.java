package com.sangdari.domain.auth.services;

import com.sangdari.domain.auth.mapper.AuthMapper;
import com.sangdari.domain.auth.requests.LoginRequest;
import com.sangdari.domain.auth.requests.SignupRequest;
import com.sangdari.domain.auth.responses.AuthResponse;
import com.sangdari.domain.user.entities.User;
import com.sangdari.domain.user.mapper.UserMapper;
import com.sangdari.domain.user.responses.UserResponse;
import com.sangdari.global.exception.custom.*;
import com.sangdari.global.security.cookie.CookieManager;
import com.sangdari.global.security.jwt.JwtConfig;
import com.sangdari.global.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final JwtConfig jwtConfig;
    private final JwtProvider jwtProvider;
    private final CookieManager cookieManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public AuthResponse login(HttpServletResponse response, LoginRequest loginRequest) {
        User user = userMapper.findByEmail(loginRequest.email());

        if(user == null) {
            throw new AuthLoginFailedException();
        }

        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new AuthLoginFailedException();
        }

        return this.generateAuthentication(response, user);
    }

    @Transactional(rollbackFor = Exception.class)
    public AuthResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshTokenOptional = jwtProvider.extractRefreshToken(request);
        if(refreshTokenOptional.isEmpty()) {
            throw new InvalidTokenException("토큰이 없습니다.");
        }
        String extractRefreshToken = refreshTokenOptional.get();

        long userId = Long.parseLong(jwtProvider.extractClaims(extractRefreshToken).getSubject());

        User user = userMapper.findByPk(userId);

        if(user == null || user.getRefreshToken() == null) {
            throw new InvalidTokenException("유효하지 않은 회원의 토큰입니다.");
        }

        if(!user.getRefreshToken().equals(extractRefreshToken)) {
            throw new InvalidTokenException("토큰이 일치하지 않습니다.");
        }

        return this.generateAuthentication(response, user);
    }

    private AuthResponse generateAuthentication(HttpServletResponse response, User user) {
        String newAccessToken = jwtProvider.createAccessToken(user);
        String newRefreshToken = jwtProvider.createRefreshToken(user);

        authMapper.updateRefreshToken(user.getUserId(), newRefreshToken);

        cookieManager.setCookie(
                response
                , jwtConfig.refreshTokenCookieName()
                , newRefreshToken
                , jwtConfig.refreshTokenCookieExpiry()
                , jwtConfig.reissUri()
        );

        // 리턴
        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .userResponse(
                UserResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .createdAt(user.getCreatedAt())
                    .build()
            )
            .build();
    }

    // 회원가입
    @Transactional(rollbackFor = Exception.class)
    public void signup(SignupRequest signupRequest) {
        User userEmail = userMapper.findByEmail(signupRequest.email());
        User userPhone = userMapper.findByPhone(signupRequest.phone());

        if (userEmail != null) {
            throw new UserEmailDuplicatedException();
        }

        if(!signupRequest.password().equals(signupRequest.passwordChk())){
            throw new UserPasswordMismatchException();
        }

        if (userPhone != null) {
            throw new UserPhoneDuplicatedException();
        }

        User newUser = User.builder()
            .email(signupRequest.email())
            .password(passwordEncoder.encode(signupRequest.password()))
            .name(signupRequest.name())
            .phone(signupRequest.phone())
            .build();

        authMapper.insertUser(newUser);
    }

    public boolean checkEmail(String email) {
        return userMapper.findByEmail(email) == null;
    }
}
