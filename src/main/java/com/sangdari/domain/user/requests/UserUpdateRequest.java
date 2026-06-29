package com.sangdari.domain.user.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRequest(
    @NotBlank(message = "이름을 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z가-힣]{2,40}$", message = "성함은 공백 없이 2~40자여야 합니다.")
    String name,

    @NotBlank(message = "연락처를 입력해주세요.")
    @Pattern(regexp = "^\\d{11}$", message = "연락처는 하이픈(-) 제외 숫자 11자리여야 합니다.")
    String phone
) {

}