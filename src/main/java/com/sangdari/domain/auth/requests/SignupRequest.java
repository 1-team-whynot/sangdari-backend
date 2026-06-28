package com.sangdari.domain.auth.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SignupRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Pattern(regexp = "^[0-9a-zA-Z](?!.*?[\\-_.]{2})[a-zA-Z0-9\\-_.]{3,63}@[0-9a-zA-Z](?!.*?[\\-_.]{2})[a-zA-Z0-9\\-_.]{3,63}\\.[a-zA-Z]{2,3}$", message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^[0-9a-zA-Z!@#$%^&*]{6,20}$", message = "비밀번호는 6~20자이며, 대/소문자, 숫자, 특수문자를 모두 포함해야 합니다.")
        String password,

        @NotBlank(message = "비밀번호를 한 번 더 입력해주세요.")
        String passwordChk,

        @NotBlank(message = "이름을 입력해주세요.")
        @Pattern(regexp = "^[A-Za-z가-힣]{2,40}$", message = "성함은 최소 2자 이상이며 공백을 포함할 수 없습니다.")
        String name,

        @NotBlank(message = "연락처를 입력해주세요.")
        @Pattern(regexp = "^\\d{11}$", message = "연락처는 하이픈(-) 제외 숫자만 입력해주세요.")
        String phone
) {
}
