package com.sangdari.domain.auth.services;

import com.sangdari.domain.auth.mapper.AuthMapper;
import com.sangdari.domain.auth.requests.LoginRequest;
import com.sangdari.domain.auth.requests.RegistrationRequest;
import com.sangdari.domain.auth.responses.AuthResponse;
import com.sangdari.domain.user.entities.User;
import com.sangdari.domain.user.mapper.UserMapper;
import com.sangdari.domain.user.responses.UserResponse;
import com.sangdari.global.errors.custom.DuplicatedUserException;
import com.sangdari.global.errors.custom.NotRegisteredException;
import com.sangdari.global.security.cookie.CookieManager;
import com.sangdari.global.security.jwt.JwtConfig;
import com.sangdari.global.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;
    private final AuthMapper authMapper;
    private final CookieManager cookieManager;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public void registration(RegistrationRequest registrationRequest) {
        // 회원가입할 유저의 정보가 DB에 이미 존재하는지 확인하기 위한 email 조회
        User user = userMapper.findByEmail(registrationRequest.email());

        if (user != null) {
            throw new DuplicatedUserException("이미 가입된 회원입니다.");
        }

        User newUser = User.builder()
                .email(registrationRequest.email())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .name(registrationRequest.name())
                .phone(registrationRequest.phone())
                .build();

        authMapper.create(newUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public AuthResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        // 유저 정보 획득
        User user = userMapper.findByEmail(loginRequest.email());

        // 유저 가입 여부 확인
        if (user == null) {
            throw new NotRegisteredException("가입된 계정이 아닙니다.");
        }

        // 비밀번호 체크
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword()))
            throw new BadCredentialsException("아이디와 비밀번호를 다시 확인해주세요.");

        return this.generateAuthentication(response, user);
    }

    private AuthResponse generateAuthentication(HttpServletResponse response, User user) {
        // 토큰 생성
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        // refresh 토큰 -> DB 저장
        authMapper.updateRefreshToken(user.getUserId(), newRefreshToken);

        // refresh 토큰 -> Cookie에 저장
        cookieManager.setCookie(
                response
                , jwtConfig.refreshTokenCookieName()
                , newRefreshToken
                , jwtConfig.refreshTokenCookieExpiry()
                , jwtConfig.reissueUri());

        // 리턴
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .user(
                        UserResponse.builder()
                                .userId(user.getUserId())
                                .email(user.getEmail())
                                .name(user.getName())
                                .createdAt(user.getCreatedAt())
                                .build()
                )
                .build();
    }
}
