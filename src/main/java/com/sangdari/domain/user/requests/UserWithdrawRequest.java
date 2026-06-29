package com.sangdari.domain.user.requests;

import jakarta.validation.constraints.NotBlank;

public record UserWithdrawRequest(
        @NotBlank(message = "본인 확인을 위해 현재 비밀번호를 입력해주세요.")
        String password
) {
}