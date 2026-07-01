package com.sangdari.domain.user.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserPasswordChangeRequest(
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    String currentPassword,

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(
        regexp = "^[0-9a-zA-Z!@#$%^&*]{6,20}$",
        message = "새 비밀번호 형식이 올바르지 않습니다."
    )
    String newPassword
) {
}
