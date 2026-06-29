package com.sangdari.domain.store.requests;

import jakarta.validation.constraints.*;

public record AllStoreListReq(
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
        Integer page,

        @Min(value = 1, message = "출력 개수는 1 이상이어야 합니다.")
        Integer limit

) {
    public AllStoreListReq(Integer page, Integer limit) {
        this.page = (page != null && page > 0) ? page : 1;
        this.limit = (limit != null && limit > 0) ? limit : 6;
    }

    // 쿼리에서 사용할 offset 구하기
    public int getOffset() {
        return (this.page - 1) * this.limit;
    }
}
