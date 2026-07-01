package com.sangdari.domain.auth.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthUserVerifyRequest(
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,

    @NotBlank(message = "이름을 입력해주세요.")
    String name,

    @NotBlank(message = "연락처를 입력해주세요.")
    String phone
) {}
