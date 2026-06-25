package com.sangdari.domain.store.requests;

import jakarta.validation.constraints.*;

import java.lang.reflect.Array;
import java.util.List;

public record StoreListReq(
        @NotNull(message = "지역 정보는 필수입니다.")
        @Min(value = 1, message = "1 이상 숫자만 허용합니다.")
        Integer regionId,

        String regionDetail,

        @NotNull(message = "날짜는 필수입니다.")
        @Pattern(
                regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
                message = "날짜 형식(YYYY-MM-DD)이 올바르지 않습니다."
        )
        String date,

        @NotNull(message = "최소 인원수는 필수입니다.")
        @PositiveOrZero(message = "인원수는 0명 이상이어야 합니다.")
        Integer minHeadcount,

        @NotNull(message = "최대 인원수는 필수입니다.")
        @PositiveOrZero(message = "인원수는 0명 이상이어야 합니다.")
        Integer maxHeadcount,

        @NotNull(message = "카테고리 리스트는 필수입니다.")
        @NotEmpty(message = "최소 하나의 카테고리를 선택해주세요.")
        List<String> selectedCategories,

        @NotNull(message = "전기 이용 가능 여부를 선택해주세요.")
        Boolean isPowerAvailable
) {
    // minHeadcount보다 maxHeadcount가 더 커야 함
    // @AssertTrue: 이 메소드나 필드의 결과가 반드시 true여야 한다
    @AssertTrue(message = "최대 인원수는 최소 인원수보다 크거나 같아야 합니다.")
    public boolean isHeadcountValid() {
        if (minHeadcount == null || maxHeadcount == null) {
            return true;
        }
        return maxHeadcount >= minHeadcount;
    }
}
