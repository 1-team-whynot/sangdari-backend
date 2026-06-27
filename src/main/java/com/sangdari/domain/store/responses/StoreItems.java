package com.sangdari.domain.store.responses;

import lombok.Builder;

// 업체 정보: 업체ID, 가게사진, 상호명, 업체소개, 음식 카테고리-카테고리명, 지역, 최소인원, 최대인원, 베터리 지원여부
@Builder
public record StoreItems(
        Long storeId
        , String imageUrl
        , String businessName
        , String storeDesc
        , String name
        , String addrBase
        , Integer minHeadcount
        , Integer maxHeadcount
        , Boolean isBatterySupported
) {
}
