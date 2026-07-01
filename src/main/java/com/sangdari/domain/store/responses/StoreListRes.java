package com.sangdari.domain.store.responses;

import lombok.Builder;

import java.util.List;

// 반환할 객체: 업체 정보(StoreItems), 라스트페이지, 마지막 페이지 여부
@Builder
public record StoreListRes(
        List<StoreItems> stores
        , long totalItems
        , boolean isLastPage
) {
}
