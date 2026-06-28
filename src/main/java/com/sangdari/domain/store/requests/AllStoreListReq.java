package com.sangdari.domain.store.requests;

import jakarta.validation.constraints.*;

import java.util.List;

public record AllStoreListReq(
        @NotNull(message = "페이지 번호는 필수입니다.")
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
        Integer page,

        @NotNull(message = "페이지당 출력 개수는 필수입니다.")
        @Min(value = 1, message = "출력 개수는 1 이상이어야 합니다.")
        Integer limit

) {
    // 쿼리에서 사용할 offset 구하기
    public int getOffset() {
        if (this.page == null || this.limit == null) {
            return 0;
        }
        return (this.page - 1) * this.limit;
    }
}
