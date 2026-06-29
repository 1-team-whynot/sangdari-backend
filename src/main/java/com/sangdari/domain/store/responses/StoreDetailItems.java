package com.sangdari.domain.store.responses;

import lombok.Builder;

// 업체 상세 기본 정보: 업체ID, 가게사진, 상호명, 사업자 번호
@Builder
public record StoreDetailItems(
        Long storeId
        , String imageUrl
        , String businessName
        , String businessNumber
) {
}
