package com.sangdari.domain.store.responses;

import lombok.Builder;

import java.util.List;

// 반환할 객체: 업체 상세 기본 정보, 메뉴 목록
@Builder
public record StoreDetailRes(
        Long storeId
        , String imageUrl
        , String businessName
        , String businessNumber
        , List<StoreMenuItems> menus
) {
}
