package com.sangdari.domain.auth.services;

import com.sangdari.domain.auth.mapper.AuthMapper;
import com.sangdari.domain.auth.requests.LoginRequest;
import com.sangdari.domain.auth.requests.SignupRequest;
import com.sangdari.domain.auth.responses.AuthResponse;
import com.sangdari.domain.user.entities.User;
import com.sangdari.domain.user.mapper.UserMapper;
import com.sangdari.domain.user.responses.UserResponse;
import com.sangdari.global.exception.custom.AuthLoginFailedException;
import com.sangdari.global.exception.custom.AuthTokenExpiredException;
import com.sangdari.global.exception.custom.UserEmailDuplicatedException;
import com.sangdari.global.exception.custom.UserPasswordMismatchException;
import com.sangdari.global.exception.custom.UserPhoneDuplicatedException;
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
            throw new AuthTokenExpiredException("로그인 시간이 만료되었습니다. 다시 로그인해주세요.");
        }
        String extractRefreshToken = refreshTokenOptional.get();

        long userId = Long.parseLong(jwtProvider.extractClaims(extractRefreshToken).getSubject());

        User user = userMapper.findByPk(userId);

        if(user == null || user.getRefreshToken() == null) {
            throw new AuthTokenExpiredException("유효하지 않은 회원의 토큰입니다.");
        }

        if(!user.getRefreshToken().equals(extractRefreshToken)) {
            throw new AuthTokenExpiredException("토큰이 일치하지 않습니다.");
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
            , jwtConfig.reissueUri()
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

    // 로그아웃
    @Transactional(rollbackFor = Exception.class)
    public void logout(HttpServletResponse response, long id) {
        // 1. 유저 정보 검증
        User user = userMapper.findByPk(id);
        if(user == null) {
            throw new AuthTokenExpiredException("유효하지 않은 회원의 토큰입니다.");
        }

        // 2. DB에 저장된 리프레쉬 토큰 파기 (null로 업데이트)
        authMapper.updateRefreshToken(id, null);

        // 3. 브라우저 쿠키에 저장된 리프레쉬 토큰 파기 (만료 시간을 0초로 전달)
        cookieManager.setCookie(
                response
                , jwtConfig.refreshTokenCookieName()
                , null
                , 0
                , jwtConfig.reissueUri()
        );
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
