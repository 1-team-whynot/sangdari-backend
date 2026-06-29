package com.sangdari.domain.store.responses;

import lombok.Builder;

// 메뉴 정보: 메뉴ID, 메뉴명, 메뉴소개, 가격, 메뉴사진
@Builder
public record StoreMenuItems(
        Long menuId
        , String name
        , String description
        , Integer price
        , String imageUrl
) {
}
