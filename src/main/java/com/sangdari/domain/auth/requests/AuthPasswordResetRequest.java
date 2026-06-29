package com.sangdari.domain.auth.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthPasswordResetRequest(
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,

    @NotBlank(message = "이름을 입력해주세요.")
    String name,

    @NotBlank(message = "연락처를 입력해주세요.")
    String phone,

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(
        regexp = "^[0-9a-zA-Z!@#$%^&*]{6,20}$",
        message = "비밀번호는 6~20자이며, 대/소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    String newPassword
    ) {
}
