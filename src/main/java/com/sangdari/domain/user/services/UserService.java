package com.sangdari.domain.user.services;

import com.sangdari.domain.user.entities.User;
import com.sangdari.domain.user.mapper.UserMapper;
import com.sangdari.domain.user.requests.UserPasswordChangeRequest;
import com.sangdari.domain.user.requests.UserUpdateRequest;
import com.sangdari.domain.user.requests.UserWithdrawRequest;
import com.sangdari.domain.user.responses.UserResponse;
import com.sangdari.global.exception.custom.CommonNotFoundException;
import com.sangdari.global.exception.custom.UserCurrentPasswordMismatchException;
import com.sangdari.global.exception.custom.UserWithdrawBlockedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    // 💡 final 키워드를 명시해야 의존성 주입이 수행됩니다.
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse index(Long userId) {
        User user = userMapper.findByPk(userId);

        if (user == null) {
            // 프로젝트에 정의된 예외 또는 커스텀 예외를 활용합니다.
            throw new IllegalArgumentException("요청한 사용자 정보를 찾을 수 없습니다.");
        }

        // record 타입 빌더를 사용하여 필요한 정보만 세팅해 반환합니다.
        return UserResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .phone(user.getPhone())
            .createdAt(user.getCreatedAt())
            .build();
    }

    @Transactional
    public UserResponse update(Long userId, UserUpdateRequest request) {
        User user = userMapper.findByPk(userId);
        if (user == null) throw new CommonNotFoundException("요청한 사용자 정보를 찾을 수 없습니다.");

        userMapper.updateUserInfo(userId, request.name(), request.phone());

        return UserResponse.builder()
            .email(user.getEmail())
            .name(request.name())
            .phone(request.phone())
            .build();
    }

    @Transactional
    public void changePassword(Long userId, UserPasswordChangeRequest request) {
        User user = userMapper.findByPk(userId);
        if (user == null) throw new CommonNotFoundException("사용자를 찾을 수 없습니다."); // E02

        // 1. 현재 비밀번호 일치 검증
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new UserCurrentPasswordMismatchException();
        }

        // 2. 새 비밀번호 암호화 및 업데이트
        userMapper.updateUserPassword(userId, passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    public void withdraw(Long userId, UserWithdrawRequest request) {
        User user = userMapper.findByPk(userId);
        if (user == null) throw new UserWithdrawBlockedException();

        // 1. 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UserCurrentPasswordMismatchException();
        }

        // 2. 예약 상태 검증 (예: ReservationMapper에 진행중인 예약 유무 확인 메서드 호출)
        // boolean hasActiveReservation = reservationMapper.existsActiveReservationByUserId(userId);
        // if (hasActiveReservation) {
        //     throw new UserWithdrawBlockedException(); // E33
        // }

        // 3. Soft Delete 처리
        userMapper.withdrawUser(userId);
    }

    public void verifyPassword(Long userId, String currentPassword) {
        User user = userMapper.findByPk(userId);
        if (user == null) throw new CommonNotFoundException("사용자를 찾을 수 없습니다.");
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UserCurrentPasswordMismatchException();
        }
    }
}