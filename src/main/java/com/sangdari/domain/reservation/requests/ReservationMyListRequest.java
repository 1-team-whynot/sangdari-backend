package com.sangdari.domain.reservation.requests;

import jakarta.validation.constraints.Min;

public record ReservationMyListRequest(
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
        Integer page,

        @Min(value = 1, message = "출력 개수는 1 이상이어야 합니다.")
        Integer limit
) {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LIMIT = 10;

    public int pageOrDefault() {
        return page == null ? DEFAULT_PAGE : page;
    }

    public int limitOrDefault() {
        return limit == null ? DEFAULT_LIMIT : limit;
    }

    public int getOffset() {
        return (pageOrDefault() - 1) * limitOrDefault();
    }
}
