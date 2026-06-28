package com.sangdari.domain.store.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stores {
    private Long storeId;
    private String imageUrl;
    private String businessName;
    private String storeDesc; // 업체 소개 TODO
    private String addrBase;
    private Integer minHeadcount;
    private Integer maxHeadcount;
    private Boolean isBatterySupported;

    private String foodCategoryNames;
}
