package com.sangdari.domain.store.requests;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record StoreListReq(

        // filterParams
        @NotNull(message = "지역 정보는 필수입니다.")
        @Min(value = 1, message = "1 이상 숫자만 허용합니다.")
        Integer regionId,

        String regionDetail,

        @NotNull(message = "행사 시작 날짜는 필수입니다.")
        @Pattern(
                regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
                message = "날짜 형식(YYYY-MM-DD)이 올바르지 않습니다."
        )
        String eventStartDate,

        @NotNull(message = "행사 종료 날짜는 필수입니다.")
        @Pattern(
                regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
                message = "날짜 형식(YYYY-MM-DD)이 올바르지 않습니다."
        )
        String eventEndDate,

        @NotNull(message = "최소 인원수는 필수입니다.")
        @PositiveOrZero(message = "인원수는 0명 이상이어야 합니다.")
        Integer minHeadcount,

        @NotNull(message = "최대 인원수는 필수입니다.")
        @PositiveOrZero(message = "인원수는 0명 이상이어야 합니다.")
        Integer maxHeadcount,

        @NotNull(message = "카테고리 리스트는 필수입니다.")
        @NotEmpty(message = "최소 하나의 카테고리를 선택해주세요.")
        List<String> selectedCategories,

        @NotNull(message = "전기 사용 가능 여부를 선택해주세요.")
        Boolean isPowerAvailable,

        // 페이지네이션 처리를 위한 파라미터
        @NotNull(message = "페이지 번호는 필수입니다.")
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
        Integer page,

        @NotNull(message = "페이지당 출력 개수는 필수입니다.")
        @Min(value = 1, message = "출력 개수는 1 이상이어야 합니다.")
        Integer limit
) {
    // minHeadcount보다 maxHeadcount가 더 커야 함
    // @AssertTrue: 이 메서드나 필드의 결과가 반드시 true여야 한다
    @AssertTrue(message = "최대 인원수는 최소 인원수보다 크거나 같아야 합니다.")
    public boolean isHeadcountValid() {
        if (minHeadcount == null || maxHeadcount == null) {
            return true;
        }
        return maxHeadcount >= minHeadcount;
    }

    @AssertTrue(message = "행사 종료 날짜는 시작 날짜보다 크거나 같아야 합니다.")
    public boolean isEventDateValid() {
        if (eventStartDate == null || eventEndDate == null) {
            return true;
        }
        return eventEndDate.compareTo(eventStartDate) >= 0;
    }

    // 쿼리에서 사용할 offset 구하기
    public int getOffset() {
        if (this.page == null || this.limit == null) {
            return 0;
        }
        return (this.page - 1) * this.limit;
    }
}
