package com.sangdari.domain.store.responses;

import lombok.Builder;

@Builder
public record StoreListRes(
        // 가게사진, 상호명, 예약가능여부, 음식 카테고리: 카테고리명, 지역, 최소인원, 최대인원, 베터리 지원여부
        String imageUrl
        , String businessName
        , Boolean reservable
        , String name
        , String addrBase
        , Long minHeadcount
        , Long maxHeadcount
        , Boolean isBatterySupported
) {
}
