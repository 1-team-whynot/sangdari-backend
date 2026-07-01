package com.sangdari.domain.reservation.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReservationCreateRequest(
        @NotNull(message = "업체 번호를 입력해주세요.")
        @Positive(message = "업체 번호는 1 이상의 숫자여야 합니다.")
        Long storeId,

        @NotBlank(message = "행사 장소 기본 주소를 입력해주세요.")
        @Size(max = 100, message = "행사 장소 기본 주소는 100자 이하여야 합니다.")
        String addrBase,

        @NotBlank(message = "행사 장소 상세 주소를 입력해주세요.")
        @Size(max = 100, message = "행사 장소 상세 주소는 100자 이하여야 합니다.")
        String addrDetail,

        @NotBlank(message = "행사 시작 날짜를 입력해주세요.")
        String eventStartDate,

        @NotBlank(message = "행사 종료 날짜를 입력해주세요.")
        String eventEndDate,

        @NotNull(message = "인원수를 입력해주세요.")
        @Positive(message = "인원수는 1명 이상이어야 합니다.")
        Integer headcount,

        @Size(max = 1000, message = "요청 사항은 1000자 이하여야 합니다.")
        String requestMemo,

        @Valid
        @NotEmpty(message = "메뉴를 1개 이상 선택해주세요.")
        List<
                @NotNull(message = "메뉴 번호를 입력해주세요.")
                @Positive(message = "메뉴 번호는 1 이상의 숫자여야 합니다.")
                Long> menuIds
) {
}
